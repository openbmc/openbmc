SUMMARY = "ClamAV anti-virus utilities and scanner tools"
DESCRIPTION = "ClamAV is an open source antivirus engine for detecting trojans, viruses, malware & other malicious threats."
HOMEPAGE = "http://www.clamav.net/index.html"
SECTION = "security"
LICENSE = "GPL-2.0-only & LGPL-2.1-only & BSD-2-Clause & Zlib & Apache-2.0-with-LLVM-exception"
LIC_FILES_CHKSUM = "file://COPYING.txt;md5=2c0b5770a62017a3121c69bb9f680b0c \
                    file://COPYING/COPYING.LGPL;md5=2d5025d4aa3495befef8f17206a5b0a1 \
                    file://COPYING/COPYING.bzip2;md5=ae8d555c34b656ff864ea9437a10d3a0 \
                    file://COPYING/COPYING.zlib;md5=3648a0b9713ab246e11536055165a41a \
                    file://COPYING/COPYING.llvm;md5=c82fc668ef1809acdd0684811df93bfc \
                    file://COPYING/COPYING.unrar;md5=6a741ba21afc8b71aeaee3b5f86a8111 \
                    file://COPYING/COPYING.file;md5=e63a61022c36cff2fdfbf02dd51674bd \
                    file://COPYING/COPYING.curl;md5=be5d9e1419c4363f4b32037a2d3b7ffa \
                    "

DEPENDS = "glibc llvm libtool db openssl zlib curl libxml2 bison pcre2 json-c libcheck rust-native cargo-native libmspack"

SRC_URI = "git://github.com/Cisco-Talos/clamav;branch=rel/1.4;protocol=https \
           file://clamd.conf \
           file://freshclam.conf \
           file://volatiles.03_clamav \
           file://tmpfiles.clamav \
           "

# ClamAV version 1.4.3
SRCREV = "d8b053865fd5995f7af98bfbcd98c9a5644bfe2b"

COMPATIBLE_HOST:libc-musl:class-target = "null"
# As of 1.4.3 compilation is broken on 32-bit platforms
COMPATIBLE_HOST:arm = "null"
COMPATIBLE_HOST:mips = "null"
COMPATIBLE_HOST:powerpc = "null"
COMPATIBLE_HOST:riscv32 = "null"
COMPATIBLE_HOST:x86 = "null"

LEAD_SONAME = "libclamav.so"
SO_VER = "12.0.0"
BINCONFIG = "${bindir}/clamav-config"

inherit cmake chrpath pkgconfig useradd systemd multilib_header multilib_script cargo cargo-update-recipe-crates

# Rust code is in libclamav_rust subdirectory
CARGO_SRC_DIR = "libclamav_rust"
# Cargo.lock is in the root directory
CARGO_LOCK_PATH = "${S}/Cargo.lock"

require ${BPN}-crates.inc
require ${BPN}-git-crates.inc

UPSTREAM_CHECK_COMMITS = "1"

CLAMAV_USER ?= "clamav"
CLAMAV_GROUP ?= "clamav"

PACKAGECONFIG ?= "clamonacc \
                  ${@bb.utils.contains("DISTRO_FEATURES", "systemd", "systemd", "", d)}"
PACKAGECONFIG[milter] = "-DENABLE_MILTER=ON ,-DENABLE_MILTER=OFF, curl, curl"
PACKAGECONFIG[clamonacc] = "-DENABLE_CLAMONACC=ON ,-DENABLE_CLAMONACC=OFF,"
PACKAGECONFIG[unrar] = "-DENABLE_UNRAR=ON ,-DENABLE_UNRAR=OFF,"
PACKAGECONFIG[freshclamdnsfix] = "-DENABLE_FRESHCLAM_DNS_FIX=ON ,-DENABLE_FRESHCLAM_DNS_FIX=OFF,"
PACKAGECONFIG[systemd] = "-DENABLE_SYSTEMD=ON -DSYSTEMD_UNIT_DIR=${systemd_system_unitdir}, -DENABLE_SYSTEMD=OFF, systemd"

EXTRA_OECMAKE = "-DCMAKE_BUILD_TYPE=Release -DOPTIMIZE=ON -DENABLE_JSON_SHARED=OFF \
                 -DCLAMAV_GROUP=${CLAMAV_GROUP} -DCLAMAV_USER=${CLAMAV_USER} \
                 -DENABLE_TESTS=OFF -DBUILD_SHARED_LIBS=ON \
                 -DDO_NOT_SET_RPATH=ON \
                 -DCMAKE_INSTALL_PREFIX=${prefix} \
                 -DCMAKE_INSTALL_SYSCONFDIR=${sysconfdir} \
                 -DCMAKE_INSTALL_FULL_SYSCONFDIR=${sysconfdir} \
                 -DSYSCONFDIR=${sysconfdir} \
                 -DAPP_CONFIG_DIRECTORY=${sysconfdir}/clamav \
                 -DHAVE_SIGNED_RIGHT_SHIFT=1 \
                 -DHAVE_UNAME_SYSCALL=1 \
                 -DHAVE_FD_PASSING=1 \
                 -Dtest_run_result=0 \
                 -Dtest_run_result__TRYRUN_OUTPUT='' \
                 -DCMAKE_C_FLAGS='${CFLAGS} -Wno-error=format-truncation -Wno-error=unused-function' \
                 -DRUST_COMPILER_TARGET=${RUST_TARGET_SYS} \
                 "

MULTILIB_SCRIPTS = "${PN}-dev:${bindir}/clamav-config"

# ClamAV uses both CMake and Rust/Cargo, so we need to configure both
do_configure() {
    cargo_common_do_configure
    export RUSTFLAGS="${RUSTFLAGS}"
    cmake_do_configure
}

# Override do_compile to handle CMake build separately from Cargo
do_compile() {
    # Build with CMake (this will also trigger the Rust build via CMake)
    cmake_do_compile
}

do_compile:append() {
    # Remove build path references from Rust static library
    ${OBJCOPY} --remove-section .debug_line ${B}/libclamav_rust/target/${RUST_TARGET_SYS}/release/libclamav_rust.a || true
}

# Override do_install to use CMake install (Rust library is statically linked, no separate install needed)
do_install() {
    # Install with CMake
    cmake_do_install
}

do_install:append() {
    install -d ${D}/${sysconfdir}
    install -d ${D}/${localstatedir}/lib/clamav
    install -d ${D}${sysconfdir}/clamav ${D}${sysconfdir}/default/volatiles

    install -m 644 ${UNPACKDIR}/clamd.conf ${D}${sysconfdir}/clamav
    install -m 644 ${UNPACKDIR}/freshclam.conf ${D}${sysconfdir}/clamav
    install -m 0644 ${UNPACKDIR}/volatiles.03_clamav  ${D}${sysconfdir}/default/volatiles/03_clamav

    if [ -d ${D}${prefix}/etc ]; then
        cp -r ${D}${prefix}/etc/* ${D}${sysconfdir}/ 2>/dev/null || true
        rm -rf ${D}${prefix}/etc
    fi

    sed -i -e 's#${STAGING_DIR_HOST}##g' ${D}${libdir}/pkgconfig/libclamav.pc

    # Remove build path references from binaries
    chrpath -d ${D}${bindir}/clambc || true
    chrpath -d ${D}${bindir}/sigtool || true
    chrpath -d ${D}${libdir}/libclamav.so.${SO_VER} || true
    chrpath -d ${D}${libdir}/libfreshclam.so.* || true

    rm ${D}/${libdir}/libclamav.so
    if [ "${INSTALL_CLAMAV_CVD}" = "1" ]; then
        install -m 666 ${S}/clamav_db/* ${D}/${localstatedir}/lib/clamav/.
    fi

    rm ${D}/${libdir}/libfreshclam.so

    if ${@bb.utils.contains('DISTRO_FEATURES','systemd','true','false',d)};then
        install -d ${D}${sysconfdir}/tmpfiles.d
        install -m 0644 ${UNPACKDIR}/tmpfiles.clamav ${D}${sysconfdir}/tmpfiles.d/clamav.conf
    fi
    oe_multilib_header clamav-types.h
}

pkg_postinst:${PN}-freshclam () {
    if [ -n "$D" ]; then
        return 0
    fi

    # Ensure correct ownership on directories (volatiles may not fix existing dirs)
    if [ -d ${localstatedir}/lib/clamav ]; then
        chown -R ${CLAMAV_USER}:${CLAMAV_GROUP} ${localstatedir}/lib/clamav
    fi
    if [ -d ${localstatedir}/log/clamav ]; then
        chown -R ${CLAMAV_USER}:${CLAMAV_GROUP} ${localstatedir}/log/clamav
    fi
}

PACKAGES += "${PN}-daemon ${PN}-clamdscan ${PN}-freshclam ${PN}-libclamav ${PN}-libclammspack"

FILES:${PN} = "${bindir}/clambc ${bindir}/clamscan ${bindir}/clamsubmit ${sbindir}/clamonacc \
               ${bindir}/*sigtool ${mandir}/man1/clambc* ${mandir}/man1/clamscan* \
               ${mandir}/man1/sigtool* ${mandir}/man1/clambsubmit* \
               ${docdir}/clamav/*"

FILES:${PN}-clamdscan = "${bindir}/clamdscan \
                         ${docdir}/clamdscan/* \
                         ${mandir}/man1/clamdscan* \
                         "

FILES:${PN}-daemon = "${bindir}/clamconf ${bindir}/clamdtop ${sbindir}/clamd \
                      ${mandir}/man1/clamconf* ${mandir}/man1/clamdtop* \
                      ${mandir}/man5/clamd* ${mandir}/man8/clamd* \
                      ${sysconfdir}/clamav/clamd.conf* \
                      ${systemd_system_unitdir}/clamav-daemon/* \
                      ${docdir}/clamav-daemon/* ${sysconfdir}/clamav-daemon \
                      ${sysconfdir}/logcheck/ignore.d.server/clamav-daemon \
                      ${systemd_system_unitdir}/clamav-daemon.service \
                      ${systemd_system_unitdir}/clamav-clamonacc.service \
                      "

FILES:${PN}-freshclam = "${bindir}/freshclam \
                         ${sysconfdir}/clamav/freshclam.conf* \
                         ${sysconfdir}/clamav ${sysconfdir}/default/volatiles \
                         ${sysconfdir}/tmpfiles.d/*.conf \
                         ${localstatedir}/lib/clamav \
                         ${docdir}/${PN}-freshclam ${mandir}/man1/freshclam.* \
                         ${mandir}/man5/freshclam.conf.* \
                         ${systemd_system_unitdir}/clamav-freshclam.service \
                         ${systemd_system_unitdir}/clamav-freshclam-once.service \
                         ${systemd_system_unitdir}/clamav-freshclam-once.timer"

FILES:${PN}-libclamav = "${libdir}/libclamav.so* \
                         ${libdir}/libfreshclam.so* ${docdir}/libclamav/* \
                         "

FILES:${PN}-libclammspack = "${libdir}/libclammspack.so* \
                             ${libdir}/libmspack.so* \
                             "

FILES:${PN}-dev = "${bindir}/clamav-config ${libdir}/*.la \
                   ${libdir}/pkgconfig/*.pc \
                   ${mandir}/man1/clamav-config.* \
                   ${includedir}/*.h ${docdir}/libclamav*"

FILES:${PN}-staticdev = "${libdir}/*.a"

FILES:${PN}-doc = "${mandir}/man/* \
                   ${datadir}/man/* \
                   ${docdir}/* \
                   "

RDEPENDS:${PN} = "openssl ncurses-libncurses libxml2 libbz2 ncurses-libtinfo curl libpcre2 clamav-libclamav"
RDEPENDS:${PN}-daemon = "clamav clamav-freshclam"
RDEPENDS:${PN}-freshclam = "clamav"
RDEPENDS:${PN}-libclamav = "clamav-libclammspack"

RRECOMMENDS:${PN} = "clamav-freshclam"

RPROVIDES:${PN} += "${PN}-systemd"
RREPLACES:${PN} += "${PN}-systemd"
RCONFLICTS:${PN} += "${PN}-systemd"

SYSTEMD_PACKAGES = "${PN}-daemon ${PN}-freshclam"
SYSTEMD_SERVICE:${PN}-daemon = "clamav-daemon.service"
SYSTEMD_SERVICE:${PN}-freshclam = "clamav-freshclam.service"

USERADD_PACKAGES = "${PN}-freshclam"
GROUPADD_PARAM:${PN}-freshclam = "--system ${CLAMAV_GROUP}"
USERADD_PARAM:${PN}-freshclam = "--system -g ${CLAMAV_GROUP} --home-dir \
                                 ${localstatedir}/lib/${BPN} \
                                 --no-create-home --shell /sbin/nologin ${CLAMAV_USER}"

INSANE_SKIP:${PN}-libclamav += "dev-so"
INSANE_SKIP:${PN}-libclammspack += "dev-so"
INSANE_SKIP:${PN} += "buildpaths"
INSANE_SKIP:${PN}-libclamav += "buildpaths"
INSANE_SKIP:${PN}-staticdev += "buildpaths"
