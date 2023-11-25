SUMMARY = "Meta-perl packagegroup"

inherit packagegroup

PROVIDES = "${PACKAGES}"
PACKAGES = "\
    packagegroup-meta-perl \
    packagegroup-meta-perl-extended \
    ${@bb.utils.contains("DISTRO_FEATURES", "ptest", "packagegroup-meta-perl-ptest-packages", "", d)} \
"

RDEPENDS:packagegroup-meta-perl = "\
    adduser \
    libalgorithm-diff-perl \
    libauthen-sasl-perl \
    libauthen-radius-perl \
    libcapture-tiny-perl \
    libcgi-perl \
    libcompress-raw-bzip2-perl \
    libcompress-raw-lzma-perl \
    libcompress-raw-zlib-perl \
    libdbd-sqlite-perl \
    libclass-method-modifiers-perl \
    libdigest-hmac-perl \
    libdigest-sha1-perl \
    libconfig-autoconf-perl \
    libcrypt-openssl-guess-perl \
    libcrypt-openssl-random-perl \
    libcrypt-openssl-rsa-perl \
    libhtml-parser-perl \
    libhtml-tree-perl \
    libhtml-tagset-perl \
    libimport-into-perl \
    libio-compress-perl \
    libio-compress-lzma-perl \
    libio-socket-ssl-perl \
    libio-stringy-perl \
    libipc-signal-perl \
    libcurses-perl \
    libmime-charset-perl \
    libmime-types-perl \
    libmodule-pluggable-perl \
    libmodule-runtime-perl \
    libmodule-build-tiny-perl \
    libdata-hexdump-perl \
    libnet-dns-perl \
    libnet-libidn-perl \
    libnet-dns-sec-perl \
    libnet-ldap-perl \
    libnet-ssleay-perl \
    libnet-telnet-perl \
    libproc-waitstat-perl \
    libdevel-globaldestruction-perl \
    libenv-perl \
    libfile-slurp-perl \
    libfile-slurper-perl \
    libtext-iconv-perl \
    libtext-diff-perl \
    libtext-charwidth-perl \
    libtext-wrapi18n-perl \
    libxml-libxml-perl \
    libxml-filter-buffertext-perl \
    libxml-sax-writer-perl \
    libextutils-config-perl \
    libextutils-cppguess-perl \
    libextutils-helpers-perl \
    libextutils-installpaths-perl \
    libextutils-parsexs-perl \
    liblocale-gettext-perl \
    libmoo-perl \
    librole-tiny-perl \
    libunix-statgrab \
    libstrictures-perl \
    libsub-exporter-progressive-perl \
    libsub-uplevel-perl \
    libterm-readkey-perl \
    libtest-deep-perl \
    libtest-harness-perl \
    libtest-nowarnings-perl \
    libtest-warn-perl \
    libunicode-linebreak-perl \
"

RDEPENDS:packagegroup-meta-perl-extended = "\
    logcheck \
    mime-construct \
"

RDEPENDS:packagegroup-meta-perl-ptest-packages = "\
    libmime-types-perl-ptest \
    libio-socket-ssl-perl-ptest \
    libdata-hexdump-perl-ptest \
    libauthen-sasl-perl-ptest \
    libnet-dns-perl-ptest \
    libnet-dns-sec-perl-ptest \
    libnet-ssleay-perl-ptest \
    libtest-harness-perl-ptest \
    libdigest-sha1-perl-ptest \
    libdigest-hmac-perl-ptest \
"

EXCLUDE_FROM_WORLD = "1"

# perl-module-encode is preferred over libencode-perl
# libencode-locale-perl depends on libencode-perl
