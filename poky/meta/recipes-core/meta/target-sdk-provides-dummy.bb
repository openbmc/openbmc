DUMMYARCH = "sdk-provides-dummy-target"

DUMMYPROVIDES_PACKAGES = "\
    busybox \
    busybox-dev \
    busybox-src \
    coreutils \
    coreutils-dev \
    coreutils-src \
    bash \
    bash-dev \
    bash-src \
    perl \
    perl-dev \
    perl-src \
    perl-module-re \
    perl-module-strict \
    perl-module-vars \
    perl-module-text-wrap \
    libxml-parser-perl \
    perl-module-bytes \
    perl-module-carp \
    perl-module-config \
    perl-module-constant \
    perl-module-data-dumper \
    perl-module-errno \
    perl-module-exporter \
    perl-module-file-basename \
    perl-module-file-compare \
    perl-module-file-copy \
    perl-module-file-find \
    perl-module-file-glob \
    perl-module-file-path \
    perl-module-file-stat \
    perl-module-file-temp \
    perl-module-getopt-long \
    perl-module-io-file \
    perl-module-overload \
    perl-module-overloading \
    perl-module-posix \
    perl-module-thread-queue \
    perl-module-threads \
    perl-module-warnings \
    perl-module-warnings-register \
    pkgconfig \
    pkgconfig-dev \
    pkgconfig-src \
"

DUMMYPROVIDES = "\
    /bin/sh \
    /bin/bash \
    /usr/bin/env \
    /usr/bin/perl \
    libperl.so.5 \
    libperl.so.5()(64bit) \
"

require dummy-sdk-package.inc

SSTATE_DUPWHITELIST += "${PKGDATA_DIR}/${PN} ${PKGDATA_DIR}/runtime/${PN}"
