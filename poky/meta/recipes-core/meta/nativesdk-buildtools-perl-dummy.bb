DUMMYARCH = "buildtools-dummy-${SDKPKGSUFFIX}"

DUMMYPROVIDES_PACKAGES = "\
    nativesdk-perl \
    nativesdk-libxml-parser-perl \
    nativesdk-perl-module-bytes \
    nativesdk-perl-module-carp \
    nativesdk-perl-module-constant \
    nativesdk-perl-module-data-dumper \
    nativesdk-perl-module-errno \
    nativesdk-perl-module-exporter \
    nativesdk-perl-module-file-basename \
    nativesdk-perl-module-file-compare \
    nativesdk-perl-module-file-copy \
    nativesdk-perl-module-file-find \
    nativesdk-perl-module-file-glob \
    nativesdk-perl-module-file-path \
    nativesdk-perl-module-file-spec \
    nativesdk-perl-module-file-stat \
    nativesdk-perl-module-file-temp \
    nativesdk-perl-module-getopt-long \
    nativesdk-perl-module-io-file \
    nativesdk-perl-module-list-util \
    nativesdk-perl-module-overloading \
    nativesdk-perl-module-posix \
    nativesdk-perl-module-scalar-util \
    nativesdk-perl-module-strict \
    nativesdk-perl-module-text-parsewords \
    nativesdk-perl-module-thread-queue \
    nativesdk-perl-module-threads \
    nativesdk-perl-module-warnings \
    nativesdk-perl-module-vars \
"

DUMMYPROVIDES = "\
    /usr/bin/perl \
    "


require dummy-sdk-package.inc

inherit nativesdk
