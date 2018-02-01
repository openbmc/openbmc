SUMMARY = "An Embeddable SQL Database Engine"
HOMEPAGE = "http://www.sqlite.org/"
SECTION = "libs"
DEPENDS = "readline ncurses"
LICENSE = "PD"
LIC_FILES_CHKSUM = "file://src/main.c;beginline=1;endline=10;md5=e98469a8efa024a38ad5b2e4b92f4a96"

PR = "r7"

SRC_URI = "http://www.hwaci.com/sw/sqlite/sqlite-${PV}.tar.gz \
           file://mainmk_build_dynamic.patch \
           file://mainmk_no_tcl.patch \
           file://sqlite.pc \
           file://0001-shell.c-Fix-format-not-a-string-literal-warning.patch \
           "

SOURCES = "attach.o auth.o btree.o btree_rb.o build.o copy.o date.o delete.o \
           expr.o func.o hash.o insert.o main.o opcodes.o os.o pager.o \
           parse.o pragma.o printf.o random.o select.o table.o tokenize.o \
           trigger.o update.o util.o vacuum.o vdbe.o vdbeaux.o where.o"

inherit autotools pkgconfig

do_configure() {
    echo "main.mk is patched, no need to configure"
    # make pkgconfig.bbclass pick this up
    cp ${WORKDIR}/sqlite.pc ${S}
}

do_compile() {
    oe_runmake -f ${S}/Makefile.linux-gcc \
             TOP="${S}" \
             BCC="${BUILD_CC}" \
             TCC="${CC}" \
             OPTS="-fPIC -D'INTPTR_TYPE=int'" \
             TCL_FLAGS= LIBTCL= \
             READLINE_FLAGS="-DHAVE_READLINE=1 -I${STAGING_INCDIR}" \
             LIBREADLINE="-L. -L${STAGING_LIBDIR} -lreadline -lncurses"
}

do_install() {
    install -d ${D}${libdir} ${D}${bindir}
    install sqlite ${D}${bindir}
    install -m 0755 libsqlite.so ${D}${libdir}/libsqlite.so.0.8.6
    ln -sf libsqlite.so.0.8.6 ${D}${libdir}/libsqlite.so
    ln -sf libsqlite.so.0.8.6 ${D}${libdir}/libsqlite.so.0
    ln -sf libsqlite.so.0.8.6 ${D}${libdir}/libsqlite.so.0.8
    install -d ${D}${includedir}
    install -m 0644 sqlite.h ${D}${includedir}/sqlite.h
    install -d ${D}${libdir}/pkgconfig
    install -m 0644 ${S}/sqlite.pc ${D}${libdir}/pkgconfig/sqlite.pc
}

PACKAGES += "${PN}-bin"
FILES_${PN}-bin = "${bindir}/*"
FILES_${PN} = "${libdir}/*.so.*"

SRC_URI[md5sum] = "838dbac20b56d2c4292e98848505a05b"
SRC_URI[sha256sum] = "3f35ebfb67867fb5b583a03e480f900206af637efe7179b32294a6a0cf806f37"

BBCLASSEXTEND = "native"
