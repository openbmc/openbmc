#!/bin/sh

#oscap oval eval --result-file ./myresults.xml ./OpenEmbedded_nodistro_0.xml

oscap xccdf eval --results results.xml --report report.html OpenEmbedded_nodistro_0.xccdf.xml
