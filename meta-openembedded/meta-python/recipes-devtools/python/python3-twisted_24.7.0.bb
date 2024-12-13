DESCRIPTION = "Twisted is an event-driven networking framework written in Python and licensed under the LGPL. \
Twisted supports TCP, UDP, SSL/TLS, multicast, Unix sockets, a large number of protocols                   \
(including HTTP, NNTP, IMAP, SSH, IRC, FTP, and others), and much more."
HOMEPAGE = "https://twisted.org"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=c1c5d2c2493b848f83864bdedd67bbf5"

SRC_URI[sha256sum] = "5a60147f044187a127ec7da96d170d49bcce50c6fd36f594e60f4587eff4d394"

inherit pypi python_hatchling

do_install:append() {
    # remove some useless files before packaging
    find ${D} \( -name "*.bat" -o -name "*.c" -o -name "*.h" \) -exec rm -f {} \;
}

PACKAGES =+ "\
    ${PN}-zsh \
    ${PN}-test \
    ${PN}-protocols \
    ${PN}-conch \
    ${PN}-mail \
    ${PN}-names \
    ${PN}-runner \
    ${PN}-web \
    ${PN}-words \
    ${PN}-pair \
    ${PN}-core \
"

DEPENDS += " \
    python3-hatch-fancy-pypi-readme-native \
    python3-incremental-native \
"

RDEPENDS:${PN} = "\
    ${PN}-core \
    ${PN}-conch \
    ${PN}-mail \
    ${PN}-names \
    ${PN}-pair \
    ${PN}-protocols \
    ${PN}-runner \
    ${PN}-web \
    ${PN}-words \
    ${PN}-zsh \
"

RDEPENDS:${PN}-core = "python3-appdirs \
                       python3-asyncio \
                       python3-attrs \
                       python3-automat \
                       python3-constantly \
                       python3-core \
                       python3-debugger \
                       python3-hyperlink \
                       python3-incremental \
                       python3-pyserial \
                       python3-typing-extensions \
                       python3-unixadmin \
                       python3-zopeinterface \
"
RDEPENDS:${PN}-test = "${PN} python3-pyhamcrest"
RDEPENDS:${PN}-conch = "${PN}-core ${PN}-protocols python3-bcrypt python3-cryptography python3-pickle"
RDEPENDS:${PN}-mail = "${PN}-core ${PN}-protocols"
RDEPENDS:${PN}-names = "${PN}-core"
RDEPENDS:${PN}-runner = "${PN}-core ${PN}-protocols"
RDEPENDS:${PN}-web += "${PN}-core ${PN}-protocols"
RDEPENDS:${PN}-words += "${PN}-core"
RDEPENDS:${PN}-pair += "${PN}-core"

FILES:${PN}-test = " \
    ${PYTHON_SITEPACKAGES_DIR}/twisted/test \
    ${PYTHON_SITEPACKAGES_DIR}/twisted/*/test \
    ${PYTHON_SITEPACKAGES_DIR}/twisted/protocols/haproxy/test/ \
"

FILES:${PN}-protocols = " \
    ${PYTHON_SITEPACKAGES_DIR}/twisted/protocols/*.py* \
    ${PYTHON_SITEPACKAGES_DIR}/twisted/protocols/haproxy \
    ${PYTHON_SITEPACKAGES_DIR}/twisted/protocols/__pycache__/*pyc \
"

FILES:${PN}-zsh = " \
    ${PYTHON_SITEPACKAGES_DIR}/twisted/python/twisted-completion.zsh \
"

FILES:${PN}-conch = " \
    ${bindir}/ckeygen \
    ${bindir}/tkconch \
    ${bindir}/conch \
    ${bindir}/cftp \
    ${PYTHON_SITEPACKAGES_DIR}/twisted/plugins/twisted_conch.py* \
    ${PYTHON_SITEPACKAGES_DIR}/twisted/plugins/__pycache__/twisted_conch*.pyc \
    ${PYTHON_SITEPACKAGES_DIR}/twisted/conch \
"

FILES:${PN}-core = " \
    ${bindir}/twist \
    ${bindir}/twistd \
    ${bindir}/trial \
    ${bindir}/pyhtmlizer \
    ${PYTHON_SITEPACKAGES_DIR}/twisted/*.py* \
    ${PYTHON_SITEPACKAGES_DIR}/twisted/application \
    ${PYTHON_SITEPACKAGES_DIR}/twisted/cred \
    ${PYTHON_SITEPACKAGES_DIR}/twisted/enterprise \
    ${PYTHON_SITEPACKAGES_DIR}/twisted/internet \
    ${PYTHON_SITEPACKAGES_DIR}/twisted/persisted \
    ${PYTHON_SITEPACKAGES_DIR}/twisted/protocols \
    ${PYTHON_SITEPACKAGES_DIR}/twisted/scripts \
    ${PYTHON_SITEPACKAGES_DIR}/twisted/spread \
    ${PYTHON_SITEPACKAGES_DIR}/twisted/tap \
    ${PYTHON_SITEPACKAGES_DIR}/twisted/trial \
    ${PYTHON_SITEPACKAGES_DIR}/twisted/*.py* \
    ${PYTHON_SITEPACKAGES_DIR}/twisted/python/*.py* \
    ${PYTHON_SITEPACKAGES_DIR}/twisted/plugins/*.py* \
    ${PYTHON_SITEPACKAGES_DIR}/twisted/logger \
    ${PYTHON_SITEPACKAGES_DIR}/twisted/_threads \
    ${PYTHON_SITEPACKAGES_DIR}/twisted/positioning \
    ${PYTHON_SITEPACKAGES_DIR}/twisted/py.typed \
    ${PYTHON_SITEPACKAGES_DIR}/twisted/__pycache__/*pyc \
    ${PYTHON_SITEPACKAGES_DIR}/twisted/python/__pycache__/*pyc \
    ${PYTHON_SITEPACKAGES_DIR}/twisted/plugins/__pycache__/__init__*.pyc \
    ${PYTHON_SITEPACKAGES_DIR}/twisted/plugins/__pycache__/cred_anonymous*.pyc \
    ${PYTHON_SITEPACKAGES_DIR}/twisted/plugins/__pycache__/cred_file*.pyc \
    ${PYTHON_SITEPACKAGES_DIR}/twisted/plugins/__pycache__/cred_memory*.pyc \
    ${PYTHON_SITEPACKAGES_DIR}/twisted/plugins/__pycache__/cred_sshkeys*.pyc \
    ${PYTHON_SITEPACKAGES_DIR}/twisted/plugins/__pycache__/cred_unix*.pyc \
    ${PYTHON_SITEPACKAGES_DIR}/twisted/plugins/__pycache__/twisted_core*.pyc \
    ${PYTHON_SITEPACKAGES_DIR}/twisted/plugins/__pycache__/twisted_ftp*.pyc \
    ${PYTHON_SITEPACKAGES_DIR}/twisted/plugins/__pycache__/twisted_inet*.pyc \
    ${PYTHON_SITEPACKAGES_DIR}/twisted/plugins/__pycache__/twisted_portforward*.pyc \
    ${PYTHON_SITEPACKAGES_DIR}/twisted/plugins/__pycache__/twisted_reactors*.pyc \
    ${PYTHON_SITEPACKAGES_DIR}/twisted/plugins/__pycache__/twisted_socks*.pyc \
    ${PYTHON_SITEPACKAGES_DIR}/twisted/plugins/__pycache__/twisted_trial*.pyc \
"

FILES:${PN}-mail = " \
    ${bindir}/mailmail \
    ${PYTHON_SITEPACKAGES_DIR}/twisted/plugins/twisted_mail.py* \
    ${PYTHON_SITEPACKAGES_DIR}/twisted/plugins/__pycache__/twisted_mail*.pyc \
    ${PYTHON_SITEPACKAGES_DIR}/twisted/mail \
"

FILES:${PN}-names = " \
    ${PYTHON_SITEPACKAGES_DIR}/twisted/plugins/twisted_names.py* \
    ${PYTHON_SITEPACKAGES_DIR}/twisted/plugins/__pycache__/twisted_names*.pyc \
    ${PYTHON_SITEPACKAGES_DIR}/twisted/names \
"

FILES:${PN}-runner = " \
    ${PYTHON_SITEPACKAGES_DIR}/twisted/plugins/twisted_runner.py* \
    ${PYTHON_SITEPACKAGES_DIR}/twisted/plugins/__pycache__/twisted_runner*.pyc \
    ${PYTHON_SITEPACKAGES_DIR}/twisted/runner \
"

FILES:${PN}-web = " \
    ${PYTHON_SITEPACKAGES_DIR}/twisted/plugins/twisted_web.py* \
    ${PYTHON_SITEPACKAGES_DIR}/twisted/plugins/__pycache__/twisted_web*.pyc \
    ${PYTHON_SITEPACKAGES_DIR}/twisted/web \
"

FILES:${PN}-words = " \
    ${PYTHON_SITEPACKAGES_DIR}/twisted/plugins/twisted_words.py* \
    ${PYTHON_SITEPACKAGES_DIR}/twisted/plugins/__pycache__/twisted_words*.pyc \
    ${PYTHON_SITEPACKAGES_DIR}/twisted/words \
"

FILES:${PN}-pair = " \
    ${PYTHON_SITEPACKAGES_DIR}/twisted/pair \
"

FILES:${PN}-doc += " \
    ${PYTHON_SITEPACKAGES_DIR}/twisted/python/_pydoctortemplates \
"

