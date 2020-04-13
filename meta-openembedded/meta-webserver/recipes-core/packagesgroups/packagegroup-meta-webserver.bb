SUMMARY = "Meta-webserver packagegroups"

inherit packagegroup

PROVIDES = "${PACKAGES}"
PACKAGES = ' \
    packagegroup-meta-webserver \
    packagegroup-meta-webserver-http \
    packagegroup-meta-webserver-php \
    packagegroup-meta-webserver-support \
    packagegroup-meta-webserver-webadmin \
'

RDEPENDS_packagegroup-meta-webserver = "\
    packagegroup-meta-webserver-http \
    packagegroup-meta-webserver-php \
    packagegroup-meta-webserver-support \
    packagegroup-meta-webserver-webadmin \
"

RDEPENDS_packagegroup-meta-webserver-http = "\
    nginx monkey hiawatha nostromo apache-websocket \
    apache2 sthttpd \
    ${@bb.utils.contains("BBFILE_COLLECTIONS", "meta-python2", "cherokee", "", d)} \
    "

RDEPENDS_packagegroup-meta-webserver-php = "\
    phpmyadmin xdebug \
    "

RDEPENDS_packagegroup-meta-webserver-support = "\
    spawn-fcgi fcgi \
    "

RDEPENDS_packagegroup-meta-webserver-webadmin = "\
    netdata webmin \
    "
EXCLUDE_FROM_WORLD = "1"
