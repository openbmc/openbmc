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
    apache-websocket \
    apache2 \
    ${@bb.utils.contains("BBFILE_COLLECTIONS", "meta-python2", "cherokee", "", d)} \
    hiawatha \
    monkey \
    nginx \
    nginx \
    nostromo \
    sthttpd \
"

RDEPENDS_packagegroup-meta-webserver-php = "\
    phpmyadmin \
    xdebug \
"

RDEPENDS_packagegroup-meta-webserver-support = "\
    spawn-fcgi \
    fcgi \
    fcgiwrap \
"

RDEPENDS_packagegroup-meta-webserver-webadmin = "\
    cockpit \
    webmin \
    netdata \
"

EXCLUDE_FROM_WORLD = "1"
