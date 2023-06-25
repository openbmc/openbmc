#!/bin/sh

oscap xccdf eval --results results.xml --report report.html --profile xccdf_org.ssgproject.content_profile_standard /usr/share/xml/scap/ssg/content/ssg-openembedded-ds.xml
