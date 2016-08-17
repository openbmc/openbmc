DESCRIPTION = "Twisted is an event-driven networking framework written in Python and licensed under the LGPL. \
Twisted supports TCP, UDP, SSL/TLS, multicast, Unix sockets, a large number of protocols                   \
(including HTTP, NNTP, IMAP, SSH, IRC, FTP, and others), and much more."
HOMEPAGE = "http://www.twistedmatrix.com"

#twisted/topfiles/NEWS:655: - Relicensed: Now under the MIT license, rather than LGPL.
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=5602d7228daf59a16f0f1b2640c46bca"

SRC_URI[md5sum] = "83fe6c0c911cc1602dbffb036be0ba79"
SRC_URI[sha256sum] = "095175638c019ac7c0604f4c291724a16ff1acd062e181b01293bf4dcbc62cf3"

PYPI_PACKAGE = "Twisted"
PYPI_PACKAGE_EXT = "tar.bz2"
inherit pypi setuptools

do_install_append() {
    # remove some useless files before packaging
    find ${D} \( -name "*.bat" -o -name "*.c" -o -name "*.h" \) -exec rm -f {} \;
}

PACKAGES += "\
    ${PN}-zsh \
    ${PN}-test \
    ${PN}-protocols \
    ${PN}-conch \
    ${PN}-lore \
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
    ${PN}-src \
    ${PN}-bin \
"

RDEPENDS_${PN} = "\
    ${PN}-bin \
    ${PN}-conch \
    ${PN}-lore \
    ${PN}-mail \
    ${PN}-names \
    ${PN}-news \
    ${PN}-runner \
    ${PN}-web \
    ${PN}-words \
"

RDEPENDS_${PN}-core = "python-core python-zopeinterface python-contextlib"
RDEPENDS_${PN}-test = "${PN}"
RDEPENDS_${PN}-conch = "${PN}-core ${PN}-protocols"
RDEPENDS_${PN}-lore = "${PN}-core"
RDEPENDS_${PN}-mail = "${PN}-core ${PN}-protocols"
RDEPENDS_${PN}-names = "${PN}-core"
RDEPENDS_${PN}-news = "${PN}-core ${PN}-protocols"
RDEPENDS_${PN}-runner = "${PN}-core ${PN}-protocols"
RDEPENDS_${PN}-web += "${PN}-core ${PN}-protocols"
RDEPENDS_${PN}-words += "${PN}-core"
RDEPENDS_${PN}-flow += "${PN}-core"
RDEPENDS_${PN}-pair += "${PN}-core"
RDEPENDS_${PN}-dbg = "${PN}"

ALLOW_EMPTY_${PN} = "1"
FILES_${PN} = ""

FILES_${PN}-test = " \
    ${libdir}/${PYTHON_DIR}/site-packages/twisted/test \
    ${libdir}/${PYTHON_DIR}/site-packages/twisted/*/test \
"

FILES_${PN}-protocols = " \
    ${libdir}/${PYTHON_DIR}/site-packages/twisted/protocols/*.py* \
    ${libdir}/${PYTHON_DIR}/site-packages/twisted/protocols/gps/ \
    ${libdir}/${PYTHON_DIR}/site-packages/twisted/protocols/mice/ \
"

FILES_${PN}-zsh = " \
    ${libdir}/${PYTHON_DIR}/site-packages/twisted/python/zsh \
    ${libdir}/${PYTHON_DIR}/site-packages/twisted/python/zshcomp.* \
    ${libdir}/${PYTHON_DIR}/site-packages/twisted/python/twisted-completion.zsh \
"

FILES_${PN}-conch = " \
    ${bindir}/ckeygen \
    ${bindir}/tkconch \
    ${bindir}/conch \
    ${bindir}/conchftp \
    ${bindir}/cftp \
    ${libdir}/${PYTHON_DIR}/site-packages/twisted/plugins/twisted_conch.py* \
    ${libdir}/${PYTHON_DIR}/site-packages/twisted/conch  \
"

FILES_${PN}-core = " \
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
"

FILES_${PN}-lore = " \
${bindir}/bookify \
${bindir}/lore \
${libdir}/${PYTHON_DIR}/site-packages/twisted/plugins/twisted_lore.py* \
${libdir}/${PYTHON_DIR}/site-packages/twisted/lore \
"

FILES_${PN}-mail = " \
${bindir}/mailmail \
${libdir}/${PYTHON_DIR}/site-packages/twisted/plugins/twisted_mail.py* \
${libdir}/${PYTHON_DIR}/site-packages/twisted/mail \
"

FILES_${PN}-names = " \
${libdir}/${PYTHON_DIR}/site-packages/twisted/plugins/twisted_names.py* \
${libdir}/${PYTHON_DIR}/site-packages/twisted/names \
"

FILES_${PN}-news = " \
${libdir}/${PYTHON_DIR}/site-packages/twisted/plugins/twisted_news.py* \
${libdir}/${PYTHON_DIR}/site-packages/twisted/news \
"

FILES_${PN}-runner = " \
${libdir}/site-packages/twisted/runner/portmap.so \
${libdir}/${PYTHON_DIR}/site-packages/twisted/runner\
"

FILES_${PN}-web = " \
${bindir}/websetroot \
${libdir}/${PYTHON_DIR}/site-packages/twisted/plugins/twisted_web.py* \
${libdir}/${PYTHON_DIR}/site-packages/twisted/web\
"

FILES_${PN}-words = " \
${bindir}/im \
${libdir}/${PYTHON_DIR}/site-packages/twisted/plugins/twisted_words.py* \
${libdir}/${PYTHON_DIR}/site-packages/twisted/words\
"

FILES_${PN}-flow = " \
${libdir}/${PYTHON_DIR}/site-packages/twisted/plugins/twisted_flow.py* \
${libdir}/${PYTHON_DIR}/site-packages/twisted/flow \"

FILES_${PN}-pair = " \
${libdir}/${PYTHON_DIR}/site-packages/twisted/plugins/twisted_pair.py* \
${libdir}/${PYTHON_DIR}/site-packages/twisted/pair \
"

FILES_${PN}-dbg += " \
${libdir}/${PYTHON_DIR}/site-packages/twisted/*/.debug \
${libdir}/${PYTHON_DIR}/site-packages/twisted/*/*/.debug \
"

RDEPENDS_{PN}-src = "${PN}"
FILES_${PN}-src = " \
	${libdir}/${PYTHON_DIR}/site-packages/twisted/*.py \
	${libdir}/${PYTHON_DIR}/site-packages/twisted/*/*.py \
	${libdir}/${PYTHON_DIR}/site-packages/twisted/*/*/*.py \
	"
