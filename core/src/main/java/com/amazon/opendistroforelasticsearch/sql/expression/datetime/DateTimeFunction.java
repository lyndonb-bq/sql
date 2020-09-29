/*
 *
 *    Copyright 2020 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License").
 *    You may not use this file except in compliance with the License.
 *    A copy of the License is located at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    or in the "license" file accompanying this file. This file is distributed
 *    on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 *    express or implied. See the License for the specific language governing
 *    permissions and limitations under the License.
 *
 */

package com.amazon.opendistroforelasticsearch.sql.expression.datetime;

import static com.amazon.opendistroforelasticsearch.sql.data.type.ExprCoreType.DATE;
import static com.amazon.opendistroforelasticsearch.sql.data.type.ExprCoreType.DATETIME;
import static com.amazon.opendistroforelasticsearch.sql.data.type.ExprCoreType.INTEGER;
import static com.amazon.opendistroforelasticsearch.sql.data.type.ExprCoreType.INTERVAL;
import static com.amazon.opendistroforelasticsearch.sql.data.type.ExprCoreType.LONG;
import static com.amazon.opendistroforelasticsearch.sql.data.type.ExprCoreType.STRING;
import static com.amazon.opendistroforelasticsearch.sql.data.type.ExprCoreType.TIME;
import static com.amazon.opendistroforelasticsearch.sql.data.type.ExprCoreType.TIMESTAMP;
import static com.amazon.opendistroforelasticsearch.sql.expression.function.BuiltinFunctionName.DAYOFMONTH;
import static com.amazon.opendistroforelasticsearch.sql.expression.function.FunctionDSL.define;
import static com.amazon.opendistroforelasticsearch.sql.expression.function.FunctionDSL.impl;
import static com.amazon.opendistroforelasticsearch.sql.expression.function.FunctionDSL.nullMissingHandling;

import com.amazon.opendistroforelasticsearch.sql.data.model.ExprDateValue;
import com.amazon.opendistroforelasticsearch.sql.data.model.ExprDatetimeValue;
import com.amazon.opendistroforelasticsearch.sql.data.model.ExprIntegerValue;
import com.amazon.opendistroforelasticsearch.sql.data.model.ExprStringValue;
import com.amazon.opendistroforelasticsearch.sql.data.model.ExprTimeValue;
import com.amazon.opendistroforelasticsearch.sql.data.model.ExprTimestampValue;
import com.amazon.opendistroforelasticsearch.sql.data.model.ExprValue;
import com.amazon.opendistroforelasticsearch.sql.expression.function.BuiltinFunctionName;
import com.amazon.opendistroforelasticsearch.sql.expression.function.BuiltinFunctionRepository;
import com.amazon.opendistroforelasticsearch.sql.expression.function.FunctionResolver;
import lombok.experimental.UtilityClass;

/**
 * The definition of date and time functions.
 * 1) have the clear interface for function define.
 * 2) the implementation should rely on ExprValue.
 */
@UtilityClass
public class DateTimeFunction {
  /**
   * Register Date and Time Functions.
   *
   * @param repository {@link BuiltinFunctionRepository}.
   */
  public void register(BuiltinFunctionRepository repository) {
    repository.register(date());
    repository.register(dayOfMonth());
    repository.register(time());
    repository.register(timestamp());
    repository.register(week());
    repository.register(adddate());
  }

  /**
   * Extracts the date part of a date and time value.
   * Also to construct a date type. The supported signatures:
   * STRING/DATE/DATETIME/TIMESTAMP -> DATE
   */
  private FunctionResolver date() {
    return define(BuiltinFunctionName.DATE.getName(),
        impl(nullMissingHandling(DateTimeFunction::exprDate), DATE, STRING),
        impl(nullMissingHandling(DateTimeFunction::exprDate), DATE, DATE),
        impl(nullMissingHandling(DateTimeFunction::exprDate), DATE, DATETIME),
        impl(nullMissingHandling(DateTimeFunction::exprDate), DATE, TIMESTAMP));
  }

  /**
   * DAYOFMONTH(DATE). return the day of the month (1-31).
   */
  private FunctionResolver dayOfMonth() {
    return define(DAYOFMONTH.getName(),
        impl(nullMissingHandling(DateTimeFunction::exprDayOfMonth),
            INTEGER, DATE),
        impl(nullMissingHandling(DateTimeFunction::exprDayOfMonth), INTEGER, STRING)
    );
  }

  /**
   * Extracts the time part of a date and time value.
   * Also to construct a time type. The supported signatures:
   * STRING/DATE/DATETIME/TIME/TIMESTAMP -> TIME
   */
  private FunctionResolver time() {
    return define(BuiltinFunctionName.TIME.getName(),
        impl(nullMissingHandling(DateTimeFunction::exprTime), TIME, STRING),
        impl(nullMissingHandling(DateTimeFunction::exprTime), TIME, DATE),
        impl(nullMissingHandling(DateTimeFunction::exprTime), TIME, DATETIME),
        impl(nullMissingHandling(DateTimeFunction::exprTime), TIME, TIME),
        impl(nullMissingHandling(DateTimeFunction::exprTime), TIME, TIMESTAMP));
  }

  /**
   * Extracts the timestamp of a date and time value.
   * Also to construct a date type. The supported signatures:
   * STRING/DATE/DATETIME/TIMESTAMP -> DATE
   */
  private FunctionResolver timestamp() {
    return define(BuiltinFunctionName.TIMESTAMP.getName(),
        impl(nullMissingHandling(DateTimeFunction::exprTimestamp), TIMESTAMP, STRING),
        impl(nullMissingHandling(DateTimeFunction::exprTimestamp), TIMESTAMP, DATE),
        impl(nullMissingHandling(DateTimeFunction::exprTimestamp), TIMESTAMP, DATETIME),
        impl(nullMissingHandling(DateTimeFunction::exprTimestamp), TIMESTAMP, TIMESTAMP));
  }

  /**
   * Specify a start date and add a temporal amount to the date.
   * The return type depends on the date type and the interval unit. Detailed supported signatures:
   * (DATE, DATETIME/TIMESTAMP, INTERVAL) -> DATETIME
   * (DATE, LONG) -> DATE
   * (DATETIME/TIMESTAMP, LONG) -> DATETIME
   */
  private FunctionResolver adddate() {
    return define(BuiltinFunctionName.ADDDATE.getName(),
        impl(nullMissingHandling(DateTimeFunction::exprAddDateInterval), DATE, DATE, INTERVAL),
        impl(nullMissingHandling(DateTimeFunction::exprAddDateInterval), DATETIME, DATE, INTERVAL),
        impl(nullMissingHandling(DateTimeFunction::exprAddDateInterval),
            DATETIME, DATETIME, INTERVAL),
        impl(nullMissingHandling(DateTimeFunction::exprAddDateInterval),
            DATETIME, TIMESTAMP, INTERVAL),
        impl(nullMissingHandling(DateTimeFunction::exprAddDateDays), DATE, DATE, LONG),
        impl(nullMissingHandling(DateTimeFunction::exprAddDateDays), DATETIME, DATETIME, LONG),
        impl(nullMissingHandling(DateTimeFunction::exprAddDateDays), DATETIME, TIMESTAMP, LONG)
    );
  }

  /**
   * WEEK(DATE[,mode]). return the week number for date.
   */
  private FunctionResolver week() {
    return define(BuiltinFunctionName.WEEK.getName(),
        impl(nullMissingHandling(DateTimeFunction::exprWeekWithoutMode), INTEGER, DATE),
        impl(nullMissingHandling(DateTimeFunction::exprWeekWithoutMode), INTEGER, DATETIME),
        impl(nullMissingHandling(DateTimeFunction::exprWeekWithoutMode), INTEGER, TIMESTAMP),
        impl(nullMissingHandling(DateTimeFunction::exprWeekWithoutMode), INTEGER, STRING),
        impl(nullMissingHandling(DateTimeFunction::exprWeek), INTEGER, DATE, INTEGER),
        impl(nullMissingHandling(DateTimeFunction::exprWeek), INTEGER, DATETIME, INTEGER),
        impl(nullMissingHandling(DateTimeFunction::exprWeek), INTEGER, TIMESTAMP, INTEGER),
        impl(nullMissingHandling(DateTimeFunction::exprWeek), INTEGER, STRING, INTEGER)
    );
  }

  /**
   * Date implementation for ExprValue.
   *
   * @param exprValue ExprValue of Date type or String type.
   * @return ExprValue.
   */
  private ExprValue exprDate(ExprValue exprValue) {
    if (exprValue instanceof ExprStringValue) {
      return new ExprDateValue(exprValue.stringValue());
    } else {
      return new ExprDateValue(exprValue.dateValue());
    }
  }

  /**
   * Day of Month implementation for ExprValue.
   * @param date ExprValue of Date type.
   * @return ExprValue.
   */
  private ExprValue exprDayOfMonth(ExprValue date) {
    if (date instanceof ExprStringValue) {
      return new ExprIntegerValue(
          new ExprDateValue(date.stringValue()).dateValue().getDayOfMonth());
    }
    return new ExprIntegerValue(date.dateValue().getDayOfMonth());
  }

  /**
   * Time implementation for ExprValue.
   * @param exprValue ExprValue of Time type or String.
   * @return ExprValue.
   */
  private ExprValue exprTime(ExprValue exprValue) {
    if (exprValue instanceof ExprStringValue) {
      return new ExprTimeValue(exprValue.stringValue());
    } else {
      return new ExprTimeValue(exprValue.timeValue());
    }
  }

  /**
   * Timestamp implementation for ExprValue.
   * @param exprValue ExprValue of Timestamp type or String type.
   * @return ExprValue.
   */
  private ExprValue exprTimestamp(ExprValue exprValue) {
    if (exprValue instanceof ExprStringValue) {
      return new ExprTimestampValue(exprValue.stringValue());
    } else {
      return new ExprTimestampValue(exprValue.timestampValue());
    }
  }

  /**
   * Week for date implementation for ExprValue.
   * When mode is not specified default value mode 0 is used for default_week_format.
   * @param date ExprValue of Date/Datetime/Timestamp/String type.
   * @return ExprValue.
   */
  private ExprValue exprWeekWithoutMode(ExprValue date) {
    return exprWeek(date, new ExprIntegerValue(0));
  }

  /**
   * Week for date implementation for ExprValue.
   * @param date ExprValue of Date/Datetime/Timestamp/String type.
   * @param mode ExprValue of Integer type.
   */
  private ExprValue exprWeek(ExprValue date, ExprValue mode) {
    CalendarLookup calendarLookup = new CalendarLookup(date);
    return new ExprIntegerValue(calendarLookup.getWeekNumber(mode.integerValue()));
  }

  /**
   * ADDDATE function implementation for ExprValue.
   *
   * @param date ExprValue of Date/Datetime/Timestamp type.
   * @param expr ExprValue of Interval type, the temporal amount to add.
   * @return Date/Datetime resulted from expr added to date.
   */
  private ExprValue exprAddDateInterval(ExprValue date, ExprValue expr) {
    return new ExprDatetimeValue(date.datetimeValue().plus(expr.intervalValue()));
  }

  /**
   * ADDDATE function implementation for ExprValue.
   *
   * @param date ExprValue of Date/Datetime/Timestamp type.
   * @param days ExprValue of Long type, representing the number of days to add.
   * @return Date/Datetime resulted from days added to date.
   */
  private ExprValue exprAddDateDays(ExprValue date, ExprValue days) {
    if (date instanceof ExprDateValue) {
      return new ExprDateValue(date.dateValue().plusDays(days.longValue()));
    }
    return new ExprDatetimeValue(date.datetimeValue().plusDays(days.longValue()));
  }
}
