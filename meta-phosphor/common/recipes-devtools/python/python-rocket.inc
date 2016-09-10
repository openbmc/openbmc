SUMMARY = "Modern, multi-threaded and extensible web server."
DESCRIPTION = "The Rocket web server is a server designed to handle the increased \
needs of modern web applications implemented in pure Python. It can serve WSGI \
applications and static files. Rocket has the ability to be extended to handle \
different types of networked request-response jobs. Rocket runs on cPython 2.5- 3.x \
and Jython 2.5 (without the need to run through the 2to3 translation tool). Rocket \
is similar in purpose to Cherrypy’s Wsgiserver but with added flexibility and concurrency."
HOMEPAGE = "http://launchpad.net/rocket"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${WORKDIR}/LICENSE;md5=b364bdf3116b1cbc1e30a6aff3224019"

inherit allarch
inherit setuptools

S = "${WORKDIR}/${SRCNAME}-${PV}"
RDEPENDS_${PN} += "\
        python-wsgiref-util \
        python-wsgiref-headers \
        python-email-utils \
        python-netserver \
        "

BBCLASSEXTEND = "nativesdk"
