SUMMARY = "Meta-perl packagegroup"

inherit packagegroup

PROVIDES = "${PACKAGES}"
PACKAGES = ' \
    packagegroup-meta-perl \
    packagegroup-meta-perl-extended \
'

RDEPENDS_packagegroup-meta-perl = "\
    libproc-waitstat-perl libmoo-perl libterm-readkey-perl \
    libunicode-linebreak-perl libcurses-perl libmime-types-perl \
    libmime-charset-perl libio-socket-ssl-perl libio-stringy-perl \
    libtext-iconv-perl libtext-charwidth-perl libtext-diff-perl \
    libtext-wrapi18n-perl liblocale-gettext-perl libdata-hexdump-perl \
    libextutils-installpaths-perl libextutils-helpers-perl libextutils-parsexs-perl \
    libextutils-config-perl libextutils-cppguess-perl libimport-into-perl \
    libcrypt-openssl-rsa-perl libcrypt-openssl-guess-perl libcrypt-openssl-random-perl \
    libxml-sax-writer-perl libxml-libxml-perl libxml-filter-buffertext-perl \
    adduser \
    libauthen-sasl-perl libnet-ldap-perl libnet-dns-perl \
    libnet-dns-sec-perl libnet-libidn-perl libnet-ssleay-perl \
    libnet-telnet-perl libdevel-globaldestruction-perl libipc-signal-perl \
    librole-tiny-perl libencode-perl libencode-locale-perl \
    libfile-slurp-perl libcapture-tiny-perl \
    po4a \
    libstrictures-perl libtest-harness-perl libsub-exporter-progressive-perl \
    libclass-method-modifiers-perl libhtml-parser-perl libhtml-tree-perl \
    libmodule-pluggable-perl libmodule-build-perl libmodule-runtime-perl \
    libmodule-build-tiny-perl libcgi-perl libalgorithm-diff-perl \
    libdbd-sqlite-perl libdigest-sha1-perl libdigest-hmac-perl \
    " 

RDEPENDS_packagegroup-meta-perl-extended = "\
    logcheck mime-construct \
    "

RDEPENDS_packagegroup-meta-perl-ptest = "\
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
