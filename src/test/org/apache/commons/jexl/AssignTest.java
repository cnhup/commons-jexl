/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.jexl;

import junit.framework.TestCase;

/**
 * Test cases for the if statement.
 * 
 * @author Dion Gillard
 * @since 1.1
 */
public class AssignTest extends TestCase {
    static JexlEngine JEXL = new JexlEngine();
    static {
        JEXL.setSilent(false);
    }
    
    public static class Froboz {
        int value;
        public Froboz(int v) {
            value = v;
        }
        public void setValue(int v) {
            value = v;
        }
        public int getValue() {
            return value;
        }
    }
    
    public static class Quux {
        String str;
        Froboz froboz;
        public Quux(String str, int fro) {
            this.str = str;
            froboz = new Froboz(fro);
        }
        
        public Froboz getFroboz() {
            return froboz;
        }
        
        public void setFroboz(Froboz froboz) {
            this.froboz = froboz;
        }
        
        public String getStr() {
            return str;
        }
        
        public void setStr(String str) {
            this.str = str;
        }
    }

    public AssignTest(String testName) {
        super(testName);
    }

    /**
     * Make sure bean assignment works
     * 
     * @throws Exception on any error
     */
    public void testAntish() throws Exception {
        Expression assign = JEXL.createExpression("froboz.value = 10");
        Expression check = JEXL.createExpression("froboz.value");
        JexlContext jc = JexlHelper.createContext();
        Object o = assign.evaluate(jc);
        assertEquals("Result is not 10", new Integer(10), o);
        o = check.evaluate(jc);
        assertEquals("Result is not 10", new Integer(10), o);
    }
    
    public void testBeanish() throws Exception {
        Expression assign = JEXL.createExpression("froboz.value = 10");
        Expression check = JEXL.createExpression("froboz.value");
        JexlContext jc = JexlHelper.createContext();
        Froboz froboz = new Froboz(-169);
        jc.getVars().put("froboz", froboz);
        Object o = assign.evaluate(jc);
        assertEquals("Result is not 10", new Integer(10), o);
        o = check.evaluate(jc);
        assertEquals("Result is not 10", new Integer(10), o);
    }
    
    public void testAmbiguous() throws Exception {
        Expression assign = JEXL.createExpression("froboz.nosuchbean = 10");
        JexlContext jc = JexlHelper.createContext();
        Froboz froboz = new Froboz(-169);
        jc.getVars().put("froboz", froboz);
        Object o = null;
        try {
            o = assign.evaluate(jc);
        }
        catch(RuntimeException xrt) {
            String str = xrt.toString();
            return;
        }
        finally {
        assertEquals("Should have failed", null, o);
        }
    }
        
    public void testArray() throws Exception {
        Expression assign = JEXL.createExpression("froboz[\"value\"] = 10");
        Expression check = JEXL.createExpression("froboz[\"value\"]");
        System.out.print(assign.dump());
        JexlContext jc = JexlHelper.createContext();
        Froboz froboz = new Froboz(0);
        jc.getVars().put("froboz", froboz);
        Object o = assign.evaluate(jc);
        assertEquals("Result is not 10", new Integer(10), o);
        o = check.evaluate(jc);
        assertEquals("Result is not 10", new Integer(10), o);
    }
    
    public void testMore() throws Exception {
        Expression assign = JEXL.createExpression("quux.froboz.value = 10");
        Expression check = JEXL.createExpression("quux[\"froboz\"].value");
        JexlContext jc = JexlHelper.createContext();
        jc.getVars().put("quux", new Quux("xuuq", 100));
        Object o = assign.evaluate(jc);
        assertEquals("Result is not 10", new Integer(10), o);
        o = check.evaluate(jc);
        assertEquals("Result is not 10", new Integer(10), o);
    }
    
    
    public void testTernary() throws Exception {
        JexlContext jc = JexlHelper.createContext();
        Expression e = JEXL.createExpression("x.y.z = foo ?'bar':'quux'");
        String canonical = e.dump();
        System.out.print(canonical);
        Object o = e.evaluate(jc);
        assertEquals("Should be quux", "quux", o);
        jc.getVars().put("foo",true);
        o = e.evaluate(jc);
        assertEquals("Should be bar", "bar", o);
        o = jc.getVars().get("x.y.z");
        assertEquals("Should be bar", "bar", o);
    }
    
    public void testNotNull() throws Exception {
        JexlContext jc = JexlHelper.createContext();
        Expression e = JEXL.createExpression("x.y.z = foo?:'quux'");
        Object o = e.evaluate(jc);
        assertEquals("Should be quux", "quux", o);
        jc.getVars().put("foo","bar");
        o = e.evaluate(jc);
        assertEquals("Should be bar", "bar", o);
        o = jc.getVars().get("x.y.z");
        assertEquals("Should be bar", "bar", o);
    }
    
    public static void main(String[] args) throws Exception {
        new AssignTest("debug").testAmbiguous();
        //new AssignTest("debug").testArray();
    }
}