#!/bin/sh

oscap oval eval \
--report oval.html \
--verbose-log-file filedevel.log \
--verbose DEVEL \
/usr/share/xml/scap/ssg/content/ssg-openembedded-ds.xml
