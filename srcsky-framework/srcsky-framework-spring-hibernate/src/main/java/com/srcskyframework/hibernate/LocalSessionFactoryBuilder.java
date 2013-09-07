package com.srcskyframework.hibernate;

import org.apache.log4j.Logger;
import org.apache.oro.text.regex.*;
import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.util.ClassUtils;

import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.MappedSuperclass;
import javax.sql.DataSource;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 13-4-18
 * Time: 下午2:05
 * To change this template use File | Settings | File Templates.
 */
public class LocalSessionFactoryBuilder extends org.springframework.orm.hibernate4.LocalSessionFactoryBuilder {

    protected Logger logger = Logger.getLogger(this.getClass());

    private static final String RESOURCE_PATTERN = "/**/*.class";

    private static final TypeFilter[] ENTITY_TYPE_FILTERS = new TypeFilter[]{
            new AnnotationTypeFilter(Entity.class, false),
            new AnnotationTypeFilter(Embeddable.class, false),
            new AnnotationTypeFilter(MappedSuperclass.class, false)};

    /**
     * Which classes are not included in the session.
     * They are some of the regular expression.
     */
    private String[] excludedClassesRegexPatterns;

    private ResourcePatternResolver resourcePatternResolver;

    public LocalSessionFactoryBuilder(DataSource dataSource) {
        super(dataSource);
    }

    public LocalSessionFactoryBuilder(DataSource dataSource, ClassLoader classLoader) {
        super(dataSource, classLoader);
    }

    public LocalSessionFactoryBuilder(DataSource dataSource, ResourceLoader resourceLoader) {
        super(dataSource, resourceLoader);
        this.resourcePatternResolver = ResourcePatternUtils.getResourcePatternResolver(resourceLoader);
    }

    public LocalSessionFactoryBuilder scanPackages(String... packagesToScan) throws HibernateException {
        try {
            for (String pkg : packagesToScan) {
                String pattern = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX +
                        ClassUtils.convertClassNameToResourcePath(pkg) + RESOURCE_PATTERN;

                Resource[] resources = this.resourcePatternResolver.getResources(pattern);
                MetadataReaderFactory readerFactory = new CachingMetadataReaderFactory(this.resourcePatternResolver);
                for (Resource resource : resources) {
                    if (resource.isReadable()) {
                        MetadataReader reader = readerFactory.getMetadataReader(resource);
                        String className = reader.getClassMetadata().getClassName();
                        if (matchesFilter(reader, readerFactory) && !isExcludedClass(className)) {
                            addAnnotatedClasses(this.resourcePatternResolver.getClassLoader().loadClass(className));
                        }
                    }
                }
            }
            return this;
        } catch (IOException ex) {
            throw new MappingException("Failed to scan classpath for unlisted classes", ex);
        } catch (ClassNotFoundException ex) {
            throw new MappingException("Failed to load annotated classes from classpath", ex);
        }
    }

    /**
     * Check whether any of the configured entity type filters matches
     * the current class descriptor contained in the metadata reader.
     */
    private boolean matchesFilter(MetadataReader reader, MetadataReaderFactory readerFactory) throws IOException {
        for (TypeFilter filter : ENTITY_TYPE_FILTERS) {
            if (filter.match(reader, readerFactory)) {
                return true;
            }
        }
        return false;
    }


    /**
     * @param excludedClassesRegexPatterns the exculdePatterns to set
     */
    public void setExcludedClassesRegexPatterns(String[] excludedClassesRegexPatterns) {
        this.excludedClassesRegexPatterns = excludedClassesRegexPatterns;
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
                Pattern pattern = compiler.compile(regex);
                if (matcher.matches(className, pattern)) {
                    logger.debug("class [{}], matches [{}], so it is excluded." + className + "," + pattern.getPattern());
                    return true;
                }
            }
        } catch (MalformedPatternException ex) {
            logger.warn("Malformed pattern [{}]" + ex.getMessage());
        }
        return false;
    }

}
