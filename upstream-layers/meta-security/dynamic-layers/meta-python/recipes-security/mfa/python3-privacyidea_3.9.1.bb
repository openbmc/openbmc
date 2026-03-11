SUMMARY = "identity, multifactor authentication (OTP), authorization, audit"
DESCRIPTION = "privacyIDEA is an open solution for strong two-factor authentication like OTP tokens, SMS, smartphones or SSH keys. Using privacyIDEA you can enhance your existing applications like local login (PAM, Windows Credential Provider), VPN, remote access, SSH connections, access to web sites or web portals with a second factor during authentication. Thus boosting the security of your existing applications."

HOMEPAGE = "http://www.privacyidea.org/"
LICENSE = "AGPL-3.0-only"
LIC_FILES_CHKSUM = "file://LICENSE;md5=c0acfa7a8a03b718abee9135bc1a1c55"

PYPI_PACKAGE = "privacyIDEA"
SRC_URI[sha256sum] = "7c70feb44980a3fd7501457777a1ec30e73541e54d3b31f2b9b5ab6cd73cff4f"

inherit pypi python_setuptools_build_meta

DEPENDS += " \
    python3-setuptools-scm-native \
"

do_install:append () {
    rm -fr ${D}${libdir}/${PYTHON_DIR}/site-packages/tests
}

USERADD_PACKAGES = "${PN}"
GROUPADD_PARAM:${PN} = "--system privacyidea"
USERADD_PARAM:${PN} = "--system -g privacyidea -o -r -d /opt/${BPN}  \
    --shell /bin/false privacyidea"

FILES:${PN} += " ${prefix}/etc/privacyidea/* ${prefix}/lib/privacyidea/*"

RDEPENDS:${PN} = " bash perl freeradius-mysql freeradius-utils"
RDEPENDS:${PN} += "python3 python3-alembic python3-babel python3-bcrypt"
RDEPENDS:${PN} += "python3-beautifulsoup4 python3-cbor2 python3-certifi python3-cffi python3-chardet"
RDEPENDS:${PN} += "python3-click python3-configobj python3-croniter python3-cryptography python3-defusedxml"
RDEPENDS:${PN} += "python3-ecdsa  python3-flask python3-flask-babel python3-flask-migrate"
RDEPENDS:${PN} += "python3-flask-script python3-flask-sqlalchemy python3-flask-versioned"
RDEPENDS:${PN} += "python3-future python3-httplib2 python3-huey python3-idna python3-ipaddress"
RDEPENDS:${PN} += "python3-itsdangerous python3-jinja2 python3-ldap python3-lxml python3-mako"
RDEPENDS:${PN} += "python3-markupsafe python3-netaddr python3-oauth2client python3-passlib python3-pillow"
RDEPENDS:${PN} += "python3-pyasn1 python3-pyasn1-modules python3-pycparser python3-pyjwt python3-pymysql"
RDEPENDS:${PN} += "python3-pyopenssl python3-pyrad python3-dateutil python3-editor python3-gnupg"
RDEPENDS:${PN} += "python3-pytz python3-pyyaml python3-qrcode python3-redis python3-requests python3-rsa"
RDEPENDS:${PN} += "python3-smpplib python3-soupsieve python3-segno python3-importlib-metadata"
RDEPENDS:${PN} += "python3-sqlalchemy python3-urllib3 python3-werkzeug"
