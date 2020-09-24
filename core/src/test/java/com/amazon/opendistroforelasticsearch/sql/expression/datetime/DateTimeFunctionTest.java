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

import static com.amazon.opendistroforelasticsearch.sql.data.model.ExprValueUtils.integerValue;
import static com.amazon.opendistroforelasticsearch.sql.data.model.ExprValueUtils.missingValue;
import static com.amazon.opendistroforelasticsearch.sql.data.model.ExprValueUtils.nullValue;
import static com.amazon.opendistroforelasticsearch.sql.data.type.ExprCoreType.DATE;
import static com.amazon.opendistroforelasticsearch.sql.data.type.ExprCoreType.DATETIME;
import static com.amazon.opendistroforelasticsearch.sql.data.type.ExprCoreType.INTEGER;
import static com.amazon.opendistroforelasticsearch.sql.data.type.ExprCoreType.INTERVAL;
import static com.amazon.opendistroforelasticsearch.sql.data.type.ExprCoreType.LONG;
import static com.amazon.opendistroforelasticsearch.sql.data.type.ExprCoreType.TIME;
import static com.amazon.opendistroforelasticsearch.sql.data.type.ExprCoreType.TIMESTAMP;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.amazon.opendistroforelasticsearch.sql.data.model.ExprDateValue;
import com.amazon.opendistroforelasticsearch.sql.data.model.ExprDatetimeValue;
import com.amazon.opendistroforelasticsearch.sql.data.model.ExprTimeValue;
import com.amazon.opendistroforelasticsearch.sql.data.model.ExprTimestampValue;
import com.amazon.opendistroforelasticsearch.sql.data.model.ExprValue;
import com.amazon.opendistroforelasticsearch.sql.exception.SemanticCheckException;
import com.amazon.opendistroforelasticsearch.sql.expression.DSL;
import com.amazon.opendistroforelasticsearch.sql.expression.Expression;
import com.amazon.opendistroforelasticsearch.sql.expression.ExpressionTestBase;
import com.amazon.opendistroforelasticsearch.sql.expression.FunctionExpression;
import com.amazon.opendistroforelasticsearch.sql.expression.env.Environment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DateTimeFunctionTest extends ExpressionTestBase {
  @Mock
  Environment<Expression, ExprValue> env;

  @Mock
  Expression nullRef;

  @Mock
  Expression missingRef;

  @BeforeEach
  public void setup() {
    when(nullRef.valueOf(env)).thenReturn(nullValue());
    when(missingRef.valueOf(env)).thenReturn(missingValue());
  }

  @Test
  public void dayOfMonth() {
    when(nullRef.type()).thenReturn(DATE);
    when(missingRef.type()).thenReturn(DATE);
    assertEquals(nullValue(), eval(dsl.dayofmonth(nullRef)));
    assertEquals(missingValue(), eval(dsl.dayofmonth(missingRef)));

    FunctionExpression expression = dsl.dayofmonth(DSL.literal(new ExprDateValue("2020-07-08")));
    assertEquals(INTEGER, expression.type());
    assertEquals("dayofmonth(DATE '2020-07-08')", expression.toString());
    assertEquals(integerValue(8), eval(expression));

    expression = dsl.dayofmonth(DSL.literal("2020-07-08"));
    assertEquals(INTEGER, expression.type());
    assertEquals("dayofmonth(\"2020-07-08\")", expression.toString());
    assertEquals(integerValue(8), eval(expression));
  }

  @Test
  public void date() {
    when(nullRef.type()).thenReturn(DATE);
    when(missingRef.type()).thenReturn(DATE);
    assertEquals(nullValue(), eval(dsl.date(nullRef)));
    assertEquals(missingValue(), eval(dsl.date(missingRef)));

    FunctionExpression expr = dsl.date(DSL.literal("2020-08-17"));
    assertEquals(DATE, expr.type());
    assertEquals(new ExprDateValue("2020-08-17"), eval(expr));
    assertEquals("date(\"2020-08-17\")", expr.toString());

    expr = dsl.date(DSL.literal(new ExprDateValue("2020-08-17")));
    assertEquals(DATE, expr.type());
    assertEquals(new ExprDateValue("2020-08-17"), eval(expr));
    assertEquals("date(DATE '2020-08-17')", expr.toString());
  }

  @Test
  public void time() {
    when(nullRef.type()).thenReturn(TIME);
    when(missingRef.type()).thenReturn(TIME);
    assertEquals(nullValue(), eval(dsl.time(nullRef)));
    assertEquals(missingValue(), eval(dsl.time(missingRef)));

    FunctionExpression expr = dsl.time(DSL.literal("01:01:01"));
    assertEquals(TIME, expr.type());
    assertEquals(new ExprTimeValue("01:01:01"), eval(expr));
    assertEquals("time(\"01:01:01\")", expr.toString());

    expr = dsl.time(DSL.literal(new ExprTimeValue("01:01:01")));
    assertEquals(TIME, expr.type());
    assertEquals(new ExprTimeValue("01:01:01"), eval(expr));
    assertEquals("time(TIME '01:01:01')", expr.toString());
  }

  @Test
  public void timestamp() {
    when(nullRef.type()).thenReturn(TIMESTAMP);
    when(missingRef.type()).thenReturn(TIMESTAMP);
    assertEquals(nullValue(), eval(dsl.timestamp(nullRef)));
    assertEquals(missingValue(), eval(dsl.timestamp(missingRef)));

    FunctionExpression expr = dsl.timestamp(DSL.literal("2020-08-17 01:01:01"));
    assertEquals(TIMESTAMP, expr.type());
    assertEquals(new ExprTimestampValue("2020-08-17 01:01:01"), expr.valueOf(env));
    assertEquals("timestamp(\"2020-08-17 01:01:01\")", expr.toString());

    expr = dsl.timestamp(DSL.literal(new ExprTimestampValue("2020-08-17 01:01:01")));
    assertEquals(TIMESTAMP, expr.type());
    assertEquals(new ExprTimestampValue("2020-08-17 01:01:01"), expr.valueOf(env));
    assertEquals("timestamp(TIMESTAMP '2020-08-17 01:01:01')", expr.toString());
  }

  @Test
  public void week() {
    when(nullRef.type()).thenReturn(DATE);
    when(missingRef.type()).thenReturn(DATE);
    assertEquals(nullValue(), eval(dsl.week(nullRef)));
    assertEquals(missingValue(), eval(dsl.week(missingRef)));

    FunctionExpression expression = dsl.week(DSL.literal(new ExprDateValue("2008-02-20")));
    assertEquals(INTEGER, expression.type());
    assertEquals("week(DATE '2008-02-20')", expression.toString());
    assertEquals(integerValue(7), eval(expression));

    expression = dsl.week(DSL.literal(new ExprDateValue("2008-02-20")), DSL.literal(0));
    assertEquals(INTEGER, expression.type());
    assertEquals("week(DATE '2008-02-20', 0)", expression.toString());
    assertEquals(integerValue(7), eval(expression));

    expression = dsl.week(DSL.literal(new ExprDateValue("2008-02-20")), DSL.literal(1));
    assertEquals(INTEGER, expression.type());
    assertEquals("week(DATE '2008-02-20', 1)", expression.toString());
    assertEquals(integerValue(8), eval(expression));

    expression = dsl.week(DSL.literal(new ExprDateValue("2008-12-31")), DSL.literal(1));
    assertEquals(INTEGER, expression.type());
    assertEquals("week(DATE '2008-12-31', 1)", expression.toString());
    assertEquals(integerValue(53), eval(expression));

    expression = dsl.week(DSL.literal(new ExprDateValue("2000-01-01")), DSL.literal(2));
    assertEquals(INTEGER, expression.type());
    assertEquals("week(DATE '2000-01-01', 2)", expression.toString());
    assertEquals(integerValue(52), eval(expression));

    expression = dsl.week(DSL.literal(new ExprDateValue("2000-01-01")), DSL.literal(7));
    assertEquals(INTEGER, expression.type());
    assertEquals("week(DATE '2000-01-01', 7)", expression.toString());
    assertEquals(integerValue(52), eval(expression));

    expression = dsl.week(DSL.literal(new ExprDateValue("2000-01-01")), DSL.literal(0));
    assertEquals(INTEGER, expression.type());
    assertEquals("week(DATE '2000-01-01', 0)", expression.toString());
    assertEquals(integerValue(0), eval(expression));

    expression = dsl.week(DSL.literal(new ExprDateValue("2000-01-01")), DSL.literal(4));
    assertEquals(INTEGER, expression.type());
    assertEquals("week(DATE '2000-01-01', 4)", expression.toString());
    assertEquals(integerValue(0), eval(expression));
  }

  @Test
  public void modeInUnsupportedFormat() {
    when(nullRef.type()).thenReturn(DATE);
    when(missingRef.type()).thenReturn(DATE);
    assertEquals(nullValue(), eval(dsl.week(nullRef)));
    assertEquals(missingValue(), eval(dsl.week(missingRef)));

    FunctionExpression expression = dsl
        .week(DSL.literal(new ExprDateValue("2000-01-01")), DSL.literal(8));
    SemanticCheckException exception =
        assertThrows(SemanticCheckException.class, () -> eval(expression));
    assertEquals("mode:8 is invalid, please use mode value between 0-7",
        exception.getMessage());
  }

  @Test
  public void adddate() {
    FunctionExpression expr = dsl.adddate(dsl.date(DSL.literal("2020-08-26")), DSL.literal(7));
    assertEquals(DATE, expr.type());
    assertEquals(new ExprDateValue("2020-09-02"), expr.valueOf(env));
    assertEquals("adddate(date(\"2020-08-26\"), 7)", expr.toString());

    expr = dsl.adddate(dsl.timestamp(DSL.literal("2020-08-26 12:05:00")), DSL.literal(7));
    assertEquals(DATETIME, expr.type());
    assertEquals(new ExprDatetimeValue("2020-09-02 12:05:00"), expr.valueOf(env));
    assertEquals("adddate(timestamp(\"2020-08-26 12:05:00\"), 7)", expr.toString());

    expr = dsl.adddate(
        dsl.date(DSL.literal("2020-08-26")), dsl.interval(DSL.literal(1), DSL.literal("hour")));
    assertEquals(DATETIME, expr.type());
    assertEquals(new ExprDatetimeValue("2020-08-26 01:00:00"), expr.valueOf(env));
    assertEquals("adddate(date(\"2020-08-26\"), interval(1, \"hour\"))", expr.toString());

    when(nullRef.type()).thenReturn(DATE);
    assertEquals(nullValue(), eval(dsl.adddate(nullRef, DSL.literal(1L))));
    assertEquals(nullValue(),
        eval(dsl.adddate(nullRef, dsl.interval(DSL.literal(1), DSL.literal("month")))));

    when(missingRef.type()).thenReturn(DATE);
    assertEquals(missingValue(), eval(dsl.adddate(missingRef, DSL.literal(1L))));
    assertEquals(missingValue(),
        eval(dsl.adddate(missingRef, dsl.interval(DSL.literal(1), DSL.literal("month")))));

    when(nullRef.type()).thenReturn(LONG);
    when(missingRef.type()).thenReturn(LONG);
    assertEquals(nullValue(), eval(dsl.adddate(dsl.date(DSL.literal("2020-08-26")), nullRef)));
    assertEquals(missingValue(),
        eval(dsl.adddate(dsl.date(DSL.literal("2020-08-26")), missingRef)));

    when(nullRef.type()).thenReturn(INTERVAL);
    when(missingRef.type()).thenReturn(INTERVAL);
    assertEquals(nullValue(), eval(dsl.adddate(dsl.date(DSL.literal("2020-08-26")), nullRef)));
    assertEquals(missingValue(),
        eval(dsl.adddate(dsl.date(DSL.literal("2020-08-26")), missingRef)));

    when(nullRef.type()).thenReturn(DATE);
    when(missingRef.type()).thenReturn(INTERVAL);
    assertEquals(missingValue(), eval(dsl.adddate(nullRef, missingRef)));
  }

  private ExprValue eval(Expression expression) {
    return expression.valueOf(env);
  }
}