/*
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.jlike.config.metrics;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Configuration;

import com.codahale.metrics.MetricRegistry;

import io.github.jlike.config.JLikeProperties;
import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.dropwizard.DropwizardExports;
import io.prometheus.client.exporter.MetricsServlet;

@Configuration
@ConditionalOnClass(CollectorRegistry.class)
public class PrometheusRegistry implements ServletContextInitializer {

    private final Logger log = LoggerFactory.getLogger(PrometheusRegistry.class);

    private final MetricRegistry metricRegistry;

    private final JLikeProperties jLikeProperties;

    public PrometheusRegistry(MetricRegistry metricRegistry, JLikeProperties jLikeProperties) {
        this.metricRegistry = metricRegistry;
        this.jLikeProperties = jLikeProperties;
    }

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        if (jLikeProperties.getMetrics().getPrometheus().isEnabled()) {
            String endpoint = jLikeProperties.getMetrics().getPrometheus().getEndpoint();
            log.info("Initializing Metrics Prometheus endpoint at {}", endpoint);
            CollectorRegistry collectorRegistry = new CollectorRegistry();
            collectorRegistry.register(new DropwizardExports(metricRegistry));
            servletContext
                .addServlet("prometheusMetrics", new MetricsServlet(collectorRegistry))
                .addMapping(endpoint);
        }
    }
}
