/**
 *    Copyright 2009-2015 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.apache.ibatis.submitted.multiple_discriminator;

import java.io.Reader;
import java.sql.Connection;
import java.sql.DriverManager;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Assert;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeClass;

public class MultipleDiscriminatorTest {
    
    private static SqlSessionFactory sqlSessionFactory;
    
    @BeforeClass
    public static void initDatabase() throws Exception {
        Connection conn = null;

        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:4000/test", "root", "");

            Reader reader = Resources.getResourceAsReader("org/apache/ibatis/submitted/multiple_discriminator/CreateDB.sql");

            ScriptRunner runner = new ScriptRunner(conn);
            runner.setLogWriter(null);
            runner.setErrorLogWriter(null);
            runner.runScript(reader);
            conn.commit();
            reader.close();

            reader = Resources.getResourceAsReader("org/apache/ibatis/submitted/multiple_discriminator/ibatisConfig.xml");
            sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
            reader.close();
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
    }

    @Test(groups={"tidb"})
    public void testMultipleDiscriminator() {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        PersonMapper personMapper = sqlSession.getMapper(PersonMapper.class);
        Person person = personMapper.get(1L);
        Assert.assertNotNull("Person must not be null", person);
        Assert.assertEquals("Person must be a director", Director.class, person.getClass());
      sqlSession.close();
    }

    @Test(groups={"tidb"})
    public void testMultipleDiscriminator2() {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        PersonMapper personMapper = sqlSession.getMapper(PersonMapper.class);
        Person person = personMapper.get2(1L);
        Assert.assertNotNull("Person must not be null", person);
        Assert.assertEquals("Person must be a director", Director.class, person.getClass());
      sqlSession.close();
    }

    @Test(groups={"tidb"}, timeOut = 20000)
    public void testMultipleDiscriminatorLoop() {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        PersonMapper personMapper = sqlSession.getMapper(PersonMapper.class);
        personMapper.getLoop();
      sqlSession.close();
      
    }
}
