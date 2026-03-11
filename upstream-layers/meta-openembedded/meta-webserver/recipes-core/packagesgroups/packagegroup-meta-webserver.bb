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

RDEPENDS:packagegroup-meta-webserver = "\
    packagegroup-meta-webserver-http \
    packagegroup-meta-webserver-php \
    packagegroup-meta-webserver-support \
    packagegroup-meta-webserver-webadmin \
"

RDEPENDS:packagegroup-meta-webserver-http = "\
    apache-websocket \
    apache2 \
    hiawatha \
    monkey \
    nginx \
    nginx \
    sthttpd \
"

RDEPENDS:packagegroup-meta-webserver-php = "\
    phpmyadmin \
    xdebug \
"

RDEPENDS:packagegroup-meta-webserver-support = "\
    spawn-fcgi \
    fcgi \
    fcgiwrap \
"

RDEPENDS:packagegroup-meta-webserver-webadmin = "\
    cockpit \
    webmin \
    netdata \
"

EXCLUDE_FROM_WORLD = "1"
