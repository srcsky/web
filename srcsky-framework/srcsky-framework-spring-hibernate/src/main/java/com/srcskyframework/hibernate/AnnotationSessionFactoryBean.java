package com.srcskyframework.hibernate;

import org.apache.log4j.Logger;
import org.apache.oro.text.regex.*;
import org.hibernate.HibernateException;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.util.CollectionUtils;

import javax.persistence.Entity;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: Zhanggaojiang
 * Date: 11-1-19
 * Time: 上午8:41
 * Email: z82422@gmail.com
 * 初始化 Hibernate Session Factory
 * 重写了 postProcessAnnotationConfiguration  方法
 * 方便Hiberante 加载 POJO对象
 */

public class AnnotationSessionFactoryBean extends LocalSessionFactoryBean {

    protected Logger logger = Logger.getLogger(getClass());
    /**
     * The locations of the hibernate enity class files. They are often some of the string with
     * Sping-style resource. A ".class" subfix can make the scaning more precise.
     * <p> example:
     * <pre>
     * classpath*:com/systop/** /model/*.class
     * </pre>
     */
    private String[] annotatedClassesLocations;

    /**
     * Which classes are not included in the session.
     * They are some of the regular expression.
     */
    private String[] excludedClassesRegexPatterns;

    /**
     * @param annotatedClassesLocations the annotatedClassesLocations to set
     */
    public void setAnnotatedClassesLocations(String[] annotatedClassesLocations) {
        this.annotatedClassesLocations = annotatedClassesLocations;
    }

    /**
     * @see org.springframework.orm.hibernate3.annotation.AnnotationSessionFactoryBean#postProcessAnnotationConfiguration(org.hibernate.cfg.AnnotationConfiguration)
     */
    public void processConfiguration() throws HibernateException {
        Set<Class> annClasses = scanAnnotatedClasses();
        if (!CollectionUtils.isEmpty(annClasses)) {
            for (Class annClass : annClasses) {
                getConfiguration().addAnnotatedClass(annClass);
            }
        }
    }

    /**
     * Scan annotated hibernate classes in the locations.
     *
     * @return Set of the annotated classes, if no matched class, return empty Set.
     */
    private Set<Class> scanAnnotatedClasses() {
        ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
        MetadataReaderFactory metadataReaderFactory = new CachingMetadataReaderFactory(resourcePatternResolver);
        Set<Class> annotatedClasses = new HashSet<Class>();
        if (annotatedClassesLocations != null) {
            try {

                for (String annClassesLocation : annotatedClassesLocations) {
                    //Resolve the resources
                    Resource[] resources = resourcePatternResolver.getResources(annClassesLocation);
                    for (Resource resource : resources) {
                        MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(resource);
                        String className = metadataReader.getClassMetadata().getClassName();
                        //If the class is hibernate enity class, and it does not match the excluded class patterns.
                        if (isEntityClass(metadataReader) && !isExcludedClass(className)) {
                            Class clazz = Class.forName(className);
                            annotatedClasses.add(clazz);
                            logger.debug("A entity class has been found. \n({})" + clazz.getName());
                        }
                    }

                }
            } catch (IOException ex) {
                logger.error("I/O failure during classpath scanning, ({})" + ex.getMessage());
                ex.printStackTrace();
            } catch (ClassNotFoundException ex) {
                logger.error("Class not found, ({})" + ex.getMessage());
                ex.printStackTrace();

            } catch (LinkageError ex) {
                logger.error("LinkageError ({})" + ex.getMessage());
                ex.printStackTrace();
            }
        }
        return annotatedClasses;
    }

    /**
     * @return True if the given MetadataReader shows
     *         that the class is annotated by <code>javax.persistence.Enity</code>
     */
    private boolean isEntityClass(MetadataReader metadataReader) {
        Set<String> annTypes = metadataReader.getAnnotationMetadata().getAnnotationTypes();
        if (CollectionUtils.isEmpty(annTypes)) {
            return false;
        }
        return annTypes.contains(Entity.class.getName());
    }

    /**
     * @return True if the given class name match the excluded class patterns.
     */
    private boolean isExcludedClass(String className) {
        if (excludedClassesRegexPatterns == null) { // All class is included.
            return false;
        }
        PatternCompiler compiler = new Perl5Compiler();
        PatternMatcher matcher = new Perl5Matcher();
        try {
            for (String regex : excludedClassesRegexPatterns) { //Test each patterns.
                logger.debug("Pattern is: {}" + regex);
                Pattern pattern = compiler.compile(regex);
                if (matcher.matches(className, pattern)) {
                    logger.debug("class [{}], matches [{}], so it is excluded." + className + pattern.getPattern());
                    return true;
                }
            }
        } catch (MalformedPatternException ex) {
            logger.warn("Malformed pattern [{}]" + ex.getMessage());
        }
        return false;
    }

    /**
     * @param excludedClassesRegexPatterns the exculdePatterns to set
     */
    public void setExcludedClassesRegexPatterns(String[] excludedClassesRegexPatterns) {
        this.excludedClassesRegexPatterns = excludedClassesRegexPatterns;
    }
}
