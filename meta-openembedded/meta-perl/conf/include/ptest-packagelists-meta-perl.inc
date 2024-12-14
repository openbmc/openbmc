#
# Lists of the ptest in meta-perl, sorted into two sets by the time they take
# Please keep these sorted in alphabetical order
#
# A first pass at getting all meta-perl recipes which inherit ptest
# meta_perl_ptest_recipes=$(bitbake-layers show-recipes --recipes-only --layer meta-perl --inherits ptest --bare | tr '\n' ' ' | pcregrep -o1 '^NOTE:.+===(.+)$')
#
# ptests which take less than ~30s each
PTESTS_FAST_META_PERL = "\
    libcapture-tiny-perl \
    libcgi-perl \
    libcrypt-openssl-random-perl \
    libdigest-hmac-perl \
    liberror-perl \
    libextutils-config-perl \
    libextutils-installpaths-perl \
    libhtml-tagset-perl \
    libmozilla-ca-perl \
    libmodule-build-tiny-perl \
    libstrictures-perl \
    libterm-readkey-perl \
    libtest-nowarnings-perl \
    libtext-diff-perl \
    liburi-perl \
"

PTESTS_SLOW_META_PERL = "\
    libio-socket-ssl-perl \
"

PTESTS_PROBLEMS_META_PERL ="\
    libauthen-sasl-perl \
    libconfig-autoconf-perl \
    libcrypt-openssl-guess-perl \
    libcrypt-openssl-rsa-perl \
    libdata-hexdump-perl \
    libdbd-sqlite-perl \
    libdigest-sha1-perl \
    libnet-dns-perl \
    libnet-dns-sec-perl \
    libnet-ldap-perl \
    libfile-slurper-perl \
    libhtml-parser-perl \
    libmime-types-perl \
    libnet-ssleay-perl \
    libsub-uplevel-perl \
    libtest-deep-perl \
    libtest-harness-perl \
    libtest-warn-perl \
    libunicode-linebreak-perl \
    libunix-statgrab \
    libxml-libxml-perl \
"
