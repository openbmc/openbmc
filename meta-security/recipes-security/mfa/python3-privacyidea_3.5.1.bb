SUMMARY = "identity, multifactor authentication (OTP), authorization, audit"
DESCRIPTION = "privacyIDEA is an open solution for strong two-factor authentication like OTP tokens, SMS, smartphones or SSH keys. Using privacyIDEA you can enhance your existing applications like local login (PAM, Windows Credential Provider), VPN, remote access, SSH connections, access to web sites or web portals with a second factor during authentication. Thus boosting the security of your existing applications."

HOMEPAGE = "http://www.privacyidea.org/"
LICENSE = "AGPL-3.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=c0acfa7a8a03b718abee9135bc1a1c55"

PYPI_PACKAGE = "privacyIDEA"
SRC_URI[sha256sum] = "c10f8e9ec681af4cb42fde70864c2b9a4b47e2bcccfc1290f83c1283748772c6"

inherit pypi setuptools3

do_install_append () {
    #install ${D}/var/log/privacyidea

    rm -fr ${D}${libdir}/${PYTHON_DIR}/site-packages/tests
}

USERADD_PACKAGES = "${PN}"
GROUPADD_PARAM_${PN} = "--system privacyidea"
USERADD_PARAM_${PN} = "--system -g privacyidea -o -r -d /opt/${BPN}  \
    --shell /bin/false privacyidea"

FILES_${PN} += " ${datadir}/etc/privacyidea/* ${datadir}/lib/privacyidea/*"

RDEPENDS_${PN} += " bash perl freeradius-mysql freeradius-utils"

RDEPENDS_${PN} += "python3 python3-alembic python3-babel python3-backports-functools-lru-cache python3-bcrypt"
RDEPENDS_${PN} += "python3-beautifulsoup4 python3-cbor2 python3-certifi python3-cffi python3-chardet"
RDEPENDS_${PN} += "python3-click python3-configobj python3-croniter python3-cryptography python3-defusedxml"
RDEPENDS_${PN} += "python3-ecdsa  python3-flask python3-flask-babel python3-flask-migrate"
RDEPENDS_${PN} += "python3-flask-script python3-flask-sqlalchemy python3-flask-versioned"
RDEPENDS_${PN} += "python3-future python3-httplib2 python3-huey python3-idna python3-ipaddress"
RDEPENDS_${PN} += "python3-itsdangerous python3-jinja2 python3-ldap python3-lxml python3-mako"
RDEPENDS_${PN} += "python3-markupsafe python3-netaddr python3-oauth2client python3-passlib python3-pillow"
RDEPENDS_${PN} += "python3-pyasn1 python3-pyasn1-modules python3-pycparser python3-pyjwt python3-pymysql"
RDEPENDS_${PN} += "python3-pyopenssl python3-pyrad python3-dateutil python3-editor python3-gnupg"
RDEPENDS_${PN} += "python3-pytz python3-pyyaml python3-qrcode python3-redis python3-requests python3-rsa"
RDEPENDS_${PN} += "python3-six python3-smpplib python3-soupsieve python3-soupsieve "
RDEPENDS_${PN} += "python3-sqlalchemy python3-sqlsoup python3-urllib3 python3-werkzeug"
