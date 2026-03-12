#
# Lists of the ptest in meta-perl, sorted into two sets by the time they take
# Please keep these sorted in alphabetical order
#
# A first pass at getting all meta-perl recipes which inherit ptest
# meta_perl_ptest_recipes=$(bitbake-layers show-recipes --recipes-only --layer meta-perl --inherits ptest --bare | tr '\n' ' ' | pcregrep -o1 '^NOTE:.+===(.+)$')
#
# ptests which take less than ~30s each
PTESTS_FAST_META_PERL = "\
    libauthen-sasl-perl \
    libcapture-tiny-perl \
    libconfig-autoconf-perl \
    libcrypt-openssl-guess-perl \
    libcrypt-openssl-random-perl \
    libcrypt-openssl-rsa-perl \
    libcgi-perl \
    libdata-hexdump-perl \
    libdbd-sqlite-perl \
    libdigest-hmac-perl \
    libdigest-sha1-perl \
    liberror-perl \
    libextutils-config-perl \
    libextutils-installpaths-perl \
    libfile-slurper-perl \
    libhtml-tagset-perl \
    libmime-base32-perl \
    libmime-types-perl \
    libmodule-build-tiny-perl \
    libmozilla-ca-perl \
    libnet-dns-perl \
    libnet-dns-sec-perl \
    libnet-ldap-perl \
    libnet-ssleay-perl \
    libstrictures-perl \
    libsub-uplevel-perl \
    libterm-readkey-perl \
    libtest-deep-perl \
    libtest-harness-perl \
    libtest-nowarnings-perl \
    libtest-warn-perl \
    libtext-diff-perl \
    libunicode-linebreak-perl \
    liburi-perl \
"

PTESTS_SLOW_META_PERL = "\
    libio-socket-ssl-perl \
"

PTESTS_PROBLEMS_META_PERL = "\
    libhtml-parser-perl \
    libunix-statgrab \
"
