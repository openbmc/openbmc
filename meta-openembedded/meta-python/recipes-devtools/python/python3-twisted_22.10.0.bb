DESCRIPTION = "Twisted is an event-driven networking framework written in Python and licensed under the LGPL. \
Twisted supports TCP, UDP, SSL/TLS, multicast, Unix sockets, a large number of protocols                   \
(including HTTP, NNTP, IMAP, SSH, IRC, FTP, and others), and much more."
HOMEPAGE = "http://www.twistedmatrix.com"

#twisted/topfiles/NEWS:655: - Relicensed: Now under the MIT license, rather than LGPL.
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=0f8d67f84b6e178c92d471011b2245fc"

SRC_URI[sha256sum] = "32acbd40a94f5f46e7b42c109bfae2b302250945561783a8b7a059048f2d4d31"

PYPI_PACKAGE = "Twisted"

inherit pypi python_setuptools_build_meta

do_install:append() {
    # remove some useless files before packaging
    find ${D} \( -name "*.bat" -o -name "*.c" -o -name "*.h" \) -exec rm -f {} \;
}

PACKAGES += "\
    ${PN}-zsh \
    ${PN}-test \
    ${PN}-protocols \
    ${PN}-conch \
    ${PN}-mail \
    ${PN}-names \
    ${PN}-news \
    ${PN}-runner \
    ${PN}-web \
    ${PN}-words \
    ${PN}-flow \
    ${PN}-pair \
    ${PN}-core \
"

PACKAGES =+ "\
    ${PN}-bin \
"

DEPENDS += " \
    ${PYTHON_PN}-incremental-native \
"

RDEPENDS:${PN} = "\
    ${PN}-bin \
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

RDEPENDS:${PN}-core = "${PYTHON_PN}-appdirs \
                       ${PYTHON_PN}-asyncio \
                       ${PYTHON_PN}-automat \
                       ${PYTHON_PN}-constantly \
                       ${PYTHON_PN}-core \
                       ${PYTHON_PN}-debugger \
                       ${PYTHON_PN}-hyperlink \
                       ${PYTHON_PN}-incremental \
                       ${PYTHON_PN}-pyhamcrest \
                       ${PYTHON_PN}-pyserial \
                       ${PYTHON_PN}-typing-extensions \
                       ${PYTHON_PN}-unixadmin \
                       ${PYTHON_PN}-zopeinterface \
"
RDEPENDS:${PN}-test = "${PN}"
RDEPENDS:${PN}-conch = "${PN}-core ${PN}-protocols ${PYTHON_PN}-bcrypt ${PYTHON_PN}-cryptography ${PYTHON_PN}-pyasn1 ${PYTHON_PN}-pickle"
RDEPENDS:${PN}-mail = "${PN}-core ${PN}-protocols"
RDEPENDS:${PN}-names = "${PN}-core"
RDEPENDS:${PN}-news = "${PN}-core ${PN}-protocols"
RDEPENDS:${PN}-runner = "${PN}-core ${PN}-protocols"
RDEPENDS:${PN}-web += "${PN}-core ${PN}-protocols"
RDEPENDS:${PN}-words += "${PN}-core"
RDEPENDS:${PN}-flow += "${PN}-core"
RDEPENDS:${PN}-pair += "${PN}-core"

FILES:${PN} = "${PYTHON_SITEPACKAGES_DIR}/${PYPI_PACKAGE}-${PV}.dist-info/*"

FILES:${PN}-test = " \
    ${libdir}/${PYTHON_DIR}/site-packages/twisted/test \
    ${libdir}/${PYTHON_DIR}/site-packages/twisted/*/test \
    ${libdir}/${PYTHON_DIR}/site-packages/twisted/protocols/haproxy/test/ \
"

FILES:${PN}-protocols = " \
    ${libdir}/${PYTHON_DIR}/site-packages/twisted/protocols/*.py* \
    ${libdir}/${PYTHON_DIR}/site-packages/twisted/protocols/gps/ \
    ${libdir}/${PYTHON_DIR}/site-packages/twisted/protocols/mice/ \
    ${libdir}/${PYTHON_DIR}/site-packages/twisted/protocols/haproxy \
"

FILES:${PN}-zsh = " \
    ${libdir}/${PYTHON_DIR}/site-packages/twisted/python/zsh \
    ${libdir}/${PYTHON_DIR}/site-packages/twisted/python/zshcomp.* \
    ${libdir}/${PYTHON_DIR}/site-packages/twisted/python/twisted-completion.zsh \
"

FILES:${PN}-conch = " \
    ${bindir}/ckeygen \
    ${bindir}/tkconch \
    ${bindir}/conch \
    ${bindir}/conchftp \
    ${bindir}/cftp \
    ${libdir}/${PYTHON_DIR}/site-packages/twisted/plugins/twisted_conch.py* \
    ${libdir}/${PYTHON_DIR}/site-packages/twisted/conch  \
"

FILES:${PN}-core = " \
${bindir}/manhole \
${bindir}/mktap \
${bindir}/twistd \
${bindir}/tap2deb \
${bindir}/tap2rpm \
${bindir}/tapconvert \
${bindir}/tkmktap \
${bindir}/trial \
${bindir}/easy_install* \
${bindir}/pyhtmlizer \
${libdir}/${PYTHON_DIR}/site-packages/twisted/python/*.so \
${libdir}/${PYTHON_DIR}/site-packages/twisted/*.py* \
${libdir}/${PYTHON_DIR}/site-packages/twisted/plugins/__init__.py* \
${libdir}/${PYTHON_DIR}/site-packages/twisted/plugins/notestplugin.py* \
${libdir}/${PYTHON_DIR}/site-packages/twisted/plugins/testplugin.py* \
${libdir}/${PYTHON_DIR}/site-packages/twisted/plugins/twisted_ftp.py* \
${libdir}/${PYTHON_DIR}/site-packages/twisted/plugins/twisted_inet.py* \
${libdir}/${PYTHON_DIR}/site-packages/twisted/plugins/twisted_manhole.py* \
${libdir}/${PYTHON_DIR}/site-packages/twisted/plugins/twisted_portforward.py* \
${libdir}/${PYTHON_DIR}/site-packages/twisted/plugins/twisted_socks.py* \
${libdir}/${PYTHON_DIR}/site-packages/twisted/plugins/twisted_telnet.py* \
${libdir}/${PYTHON_DIR}/site-packages/twisted/plugins/twisted_trial.py* \
${libdir}/${PYTHON_DIR}/site-packages/twisted/plugins/dropin.cache \
${libdir}/${PYTHON_DIR}/site-packages/twisted/application \
${libdir}/${PYTHON_DIR}/site-packages/twisted/cred \
${libdir}/${PYTHON_DIR}/site-packages/twisted/enterprise \
${libdir}/${PYTHON_DIR}/site-packages/twisted/internet \
${libdir}/${PYTHON_DIR}/site-packages/twisted/manhole \
${libdir}/${PYTHON_DIR}/site-packages/twisted/manhole \
${libdir}/${PYTHON_DIR}/site-packages/twisted/persisted \
${libdir}/${PYTHON_DIR}/site-packages/twisted/protocols\
${libdir}/${PYTHON_DIR}/site-packages/twisted/python\
${libdir}/${PYTHON_DIR}/site-packages/twisted/python/timeoutqueue.py* \
${libdir}/${PYTHON_DIR}/site-packages/twisted/python/filepath.py* \
${libdir}/${PYTHON_DIR}/site-packages/twisted/python/dxprofile.py* \
${libdir}/${PYTHON_DIR}/site-packages/twisted/python/plugin.py* \
${libdir}/${PYTHON_DIR}/site-packages/twisted/python/htmlizer.py* \
${libdir}/${PYTHON_DIR}/site-packages/twisted/python/__init__.py* \
${libdir}/${PYTHON_DIR}/site-packages/twisted/python/dispatch.py* \
${libdir}/${PYTHON_DIR}/site-packages/twisted/python/hook.py* \
${libdir}/${PYTHON_DIR}/site-packages/twisted/python/threadpool.py* \
${libdir}/${PYTHON_DIR}/site-packages/twisted/python/otp.py* \
${libdir}/${PYTHON_DIR}/site-packages/twisted/python/usage.py* \
${libdir}/${PYTHON_DIR}/site-packages/twisted/python/roots.py* \
${libdir}/${PYTHON_DIR}/site-packages/twisted/python/versions.py* \
${libdir}/${PYTHON_DIR}/site-packages/twisted/python/urlpath.py* \
${libdir}/${PYTHON_DIR}/site-packages/twisted/python/util.py* \
${libdir}/${PYTHON_DIR}/site-packages/twisted/python/components.py* \
${libdir}/${PYTHON_DIR}/site-packages/twisted/python/logfile.py* \
${libdir}/${PYTHON_DIR}/site-packages/twisted/python/runtime.py* \
${libdir}/${PYTHON_DIR}/site-packages/twisted/python/reflect.py* \
${libdir}/${PYTHON_DIR}/site-packages/twisted/python/context.py* \
${libdir}/${PYTHON_DIR}/site-packages/twisted/python/threadable.py* \
${libdir}/${PYTHON_DIR}/site-packages/twisted/python/rebuild.py* \
${libdir}/${PYTHON_DIR}/site-packages/twisted/python/failure.py* \
${libdir}/${PYTHON_DIR}/site-packages/twisted/python/lockfile.py* \
${libdir}/${PYTHON_DIR}/site-packages/twisted/python/formmethod.py* \
${libdir}/${PYTHON_DIR}/site-packages/twisted/python/finalize.py* \
${libdir}/${PYTHON_DIR}/site-packages/twisted/python/win32.py* \
${libdir}/${PYTHON_DIR}/site-packages/twisted/python/dist.py* \
${libdir}/${PYTHON_DIR}/site-packages/twisted/python/shortcut.py* \
${libdir}/${PYTHON_DIR}/site-packages/twisted/python/zipstream.py* \
${libdir}/${PYTHON_DIR}/site-packages/twisted/python/release.py* \
${libdir}/${PYTHON_DIR}/site-packages/twisted/python/syslog.py* \
${libdir}/${PYTHON_DIR}/site-packages/twisted/python/log.py* \
${libdir}/${PYTHON_DIR}/site-packages/twisted/python/compat.py* \
${libdir}/${PYTHON_DIR}/site-packages/twisted/python/zshcomp.py* \
${libdir}/${PYTHON_DIR}/site-packages/twisted/python/procutils.py* \
${libdir}/${PYTHON_DIR}/site-packages/twisted/python/text.py* \
${libdir}/${PYTHON_DIR}/site-packages/twisted/python/_twisted_zsh_stub \
${libdir}/${PYTHON_DIR}/site-packages/twisted/scripts/ \
${libdir}/${PYTHON_DIR}/site-packages/twisted/spread/ \
${libdir}/${PYTHON_DIR}/site-packages/twisted/tap/ \
${libdir}/${PYTHON_DIR}/site-packages/twisted/trial/ \
${libdir}/${PYTHON_DIR}/site-packages/twisted/__init__.py* \
${libdir}/${PYTHON_DIR}/site-packages/twisted/_version.py* \
${libdir}/${PYTHON_DIR}/site-packages/twisted/copyright.py* \
${libdir}/${PYTHON_DIR}/site-packages/twisted/im.py* \
${libdir}/${PYTHON_DIR}/site-packages/twisted/*.py* \
${libdir}/${PYTHON_DIR}/site-packages/twisted/python/*.py* \
${libdir}/${PYTHON_DIR}/site-packages/twisted/plugins/*.py* \
${libdir}/${PYTHON_DIR}/site-packages/twisted/topfiles \
${libdir}/${PYTHON_DIR}/site-packages/Twisted*egg-info \
${libdir}/${PYTHON_DIR}/site-packages/twisted/logger/ \
${libdir}/${PYTHON_DIR}/site-packages/twisted/_threads/ \
${libdir}/${PYTHON_DIR}/site-packages/twisted/positioning/ \
${libdir}/${PYTHON_DIR}/site-packages/twisted/py.typed \
"

FILES:${PN}-mail = " \
${bindir}/mailmail \
${libdir}/${PYTHON_DIR}/site-packages/twisted/plugins/twisted_mail.py* \
${libdir}/${PYTHON_DIR}/site-packages/twisted/mail \
"

FILES:${PN}-names = " \
${libdir}/${PYTHON_DIR}/site-packages/twisted/plugins/twisted_names.py* \
${libdir}/${PYTHON_DIR}/site-packages/twisted/names \
"

FILES:${PN}-news = " \
${libdir}/${PYTHON_DIR}/site-packages/twisted/plugins/twisted_news.py* \
${libdir}/${PYTHON_DIR}/site-packages/twisted/news \
"

FILES:${PN}-runner = " \
${libdir}/site-packages/twisted/runner/portmap.so \
${libdir}/${PYTHON_DIR}/site-packages/twisted/runner\
"

FILES:${PN}-web = " \
${bindir}/websetroot \
${libdir}/${PYTHON_DIR}/site-packages/twisted/plugins/twisted_web.py* \
${libdir}/${PYTHON_DIR}/site-packages/twisted/web\
"

FILES:${PN}-words = " \
${bindir}/im \
${libdir}/${PYTHON_DIR}/site-packages/twisted/plugins/twisted_words.py* \
${libdir}/${PYTHON_DIR}/site-packages/twisted/words\
"

FILES:${PN}-flow = " \
${libdir}/${PYTHON_DIR}/site-packages/twisted/plugins/twisted_flow.py* \
${libdir}/${PYTHON_DIR}/site-packages/twisted/flow \"

FILES:${PN}-pair = " \
${libdir}/${PYTHON_DIR}/site-packages/twisted/plugins/twisted_pair.py* \
${libdir}/${PYTHON_DIR}/site-packages/twisted/pair \
"

FILES:${PN}-doc += " \
    ${libdir}/${PYTHON_DIR}/site-packages/twisted/python/_pydoctortemplates/ \
"

FILES:${PN}-core:append = " \
  ${libdir}/${PYTHON_DIR}/site-packages/twisted/__pycache__ \
  ${libdir}/${PYTHON_DIR}/site-packages/twisted/python/__pycache__/*pyc \
  ${libdir}/${PYTHON_DIR}/site-packages/twisted/plugins/__pycache__/__init__*.pyc \
  ${libdir}/${PYTHON_DIR}/site-packages/twisted/plugins/__pycache__/notestplugin*.pyc \
  ${libdir}/${PYTHON_DIR}/site-packages/twisted/plugins/__pycache__/testplugin*.pyc \
  ${libdir}/${PYTHON_DIR}/site-packages/twisted/plugins/__pycache__/twisted_ftp*.pyc \
  ${libdir}/${PYTHON_DIR}/site-packages/twisted/plugins/__pycache__/twisted_inet*.pyc \
  ${libdir}/${PYTHON_DIR}/site-packages/twisted/plugins/__pycache__/twisted_manhole*.pyc \
  ${libdir}/${PYTHON_DIR}/site-packages/twisted/plugins/__pycache__/twisted_portforward*.pyc \
  ${libdir}/${PYTHON_DIR}/site-packages/twisted/plugins/__pycache__/twisted_socks*.pyc \
  ${libdir}/${PYTHON_DIR}/site-packages/twisted/plugins/__pycache__/twisted_telnet*.pyc \
  ${libdir}/${PYTHON_DIR}/site-packages/twisted/plugins/__pycache__/twisted_trial*.pyc \
  ${libdir}/${PYTHON_DIR}/site-packages/twisted/plugins/__pycache__/twisted_core*.pyc \
  ${libdir}/${PYTHON_DIR}/site-packages/twisted/plugins/__pycache__/twisted_qtstub*.pyc \
  ${libdir}/${PYTHON_DIR}/site-packages/twisted/plugins/__pycache__/twisted_reactors*.pyc \
  ${libdir}/${PYTHON_DIR}/site-packages/twisted/plugins/__pycache__/cred*.pyc \
  ${libdir}/${PYTHON_DIR}/site-packages/twisted/plugins/__pycache__/dropin*.cache \
"

FILES:${PN}-names:append = " \
  ${libdir}/${PYTHON_DIR}/site-packages/twisted/plugins/__pycache__/twisted_names*.pyc \
"

FILES:${PN}-news:append = " \
  ${libdir}/${PYTHON_DIR}/site-packages/twisted/plugins/__pycache__/twisted_news*.pyc \
"

FILES:${PN}-protocols:append = " \
  ${libdir}/${PYTHON_DIR}/site-packages/twisted/protocols/__pycache__/*pyc \
"

FILES:${PN}-conch:append = " \
  ${libdir}/${PYTHON_DIR}/site-packages/twisted/plugins/__pycache__/twisted_conch*.pyc \
"

FILES:${PN}-lore:append = " \
  ${libdir}/${PYTHON_DIR}/site-packages/twisted/plugins/__pycache__/twisted_lore*.pyc \
"
FILES:${PN}-mail:append = " \
  ${libdir}/${PYTHON_DIR}/site-packages/twisted/plugins/__pycache__/twisted_mail*.pyc \
"

FILES:${PN}-web:append = " \
  ${libdir}/${PYTHON_DIR}/site-packages/twisted/plugins/__pycache__/twisted_web*.pyc \
"

FILES:${PN}-words:append = " \
  ${libdir}/${PYTHON_DIR}/site-packages/twisted/plugins/__pycache__/twisted_words*.pyc \
"

FILES:${PN}-flow:append = " \
  ${libdir}/${PYTHON_DIR}/site-packages/twisted/plugins/__pycache__/twisted_flow*.pyc \
"

FILES:${PN}-pair:append = " \
  ${libdir}/${PYTHON_DIR}/site-packages/twisted/plugins/__pycache__/twisted_pair*.pyc \
"

FILES:${PN}-runner:append = " \
  ${libdir}/${PYTHON_DIR}/site-packages/twisted/plugins/__pycache__/twisted_runner*.pyc \
"
