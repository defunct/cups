package com.goodworkalan.cups.org.hibernate;

import java.io.File;

import com.goodworkalan.cups.remix.Sandbox;
import com.goodworkalan.go.go.Environment;
import com.goodworkalan.mix.antlr.Antlr;
import com.goodworkalan.mix.builder.Builder;
import com.goodworkalan.mix.cookbook.JavaProject;
import com.goodworkalan.spawn.Spawn;

public class HibernateCoreFormula {
    public void execute(Environment env) {
        Sandbox sandbox = env.get(Sandbox.class, 1);
        Spawn spawn = new Spawn();
        spawn.setWorkingDirectory(sandbox.directory);
        spawn.$$("svn", "checkout", "http://anonsvn.jboss.org/repos/hibernate/core/tags/hibernate-3.5.2-Final/core/", "html-cache-dtds");
    }

    /**
     * Build the project definition for Hibernate.
     *
     * @param builder
     *          The project builder.
     */
    public void build(Builder builder) {
        builder
            .cookbook(JavaProject.class)
                .produces("org.hiberate/hibernate-core/3.5.2-Final")
                .depends()
                    .production("org.jboss.javaee/jboss-jacc-api_JDK4/1.1.0",
                        "org.jboss.javaee/jboss-servlet-api_3.0",
                        "org.jboss.logging/jboss-logging-spi",
                        "org.jboss/jboss-common-core")
                    .production("antlr/antlr/2.7.6")
                    .production("dom4j/dom4j/1.6.1")
                    .production("ant/ant/1.6.5")
                    .production("org.slf4j/slf4j-api/1.5.8")
                    .production("cglib/cglib/2.2")
                    .production("javassist/javassist/3.11.0.GA")
                    .production("commons-collections/commons-collections/3.1")
                    .production("javax.transaction/jta/1.1")
                    .development("org.testng/testng-jdk15/5.10")
                    .end()
                .end()
            .end();
        builder
        	.recipe("javac")
        		.depends()
        			.recipe("antlr")
        			.end()
        		.end()
        	.end();
        builder
            .recipe("antlr")
                .task(Antlr.class)
                    .source(new File("src/main/antlr")).include("hql.g").end()
                    .output(new File("src/main/java"))
                    .traceParser()
                    .end()
                .task(Antlr.class)
                    .source(new File("src/main/antlr")).include("hql-sql.g").end()
                    .output(new File("src/main/java"))
                    .traceTreeParser()
                    .end()
                .task(Antlr.class)
                    .source(new File("src/main/antlr")).include("sql-gen.g").end()
                    .output(new File("src/main/java"))
                    .traceTreeParser()
                    .end()
                .task(Antlr.class)
                    .source(new File("src/main/antlr")).include("order-by.g").end()
                    .output(new File("src/main/java"))
                    .traceParser()
                    .end()
                .task(Antlr.class)
                    .source(new File("src/main/antlr")).include("order-by-render.g").end()
                    .output(new File("src/main/java"))
                    .traceTreeParser()
                    .end()
                .end()
            .end();
    }
}
