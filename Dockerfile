###
### automated build of gprdb by docker
###

FROM java:8
MAINTAINER Shu Tadaka <tadaka@sb.ecei.tohoku.ac.jp>

RUN mkdir /opt/gprdb

ADD . /opt/gprdb-source
RUN cd /opt/gprdb-source \
  && ./gradlew jar \
  && ./gradlew :resource:generateDependencyLicenseReport \
  && ./gradlew :gprdb:distTar :gprdb:distZip

RUN cd /opt/gprdb \
  && tar xvf /opt/gprdb-source/gprdb/build/distributions/gprdb-*.tar --strip 1
