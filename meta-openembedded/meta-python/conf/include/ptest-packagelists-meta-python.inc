#
# Lists of the ptest in meta-python, sorted into two sets by the time they take
# Please keep these sorted in alphabetical order
#
# A first pass at getting all meta-python recipes which inherit ptest
# meta_python_ptest_recipes=$(bitbake-layers show-recipes --recipes-only --layer meta-python --inherits ptest --bare | tr '\n' ' ' | pcregrep -o1 '^NOTE:.+===(.+)$')
#
# ptests which take less than ~30s each
PTESTS_FAST_META_PYTHON = "\
    python3-a2wsgi \
    python3-appdirs \
    python3-ansicolors \
    python3-asgiref \
    python3-aspectlib \
    python3-bleak \
    python3-blinker \
    python3-cachetools \
    python3-cbor2 \
    python3-click \
    python3-dominate \
    python3-execnet \
    python3-flexcache \
    python3-flexparser \
    python3-freezegun \
    python3-geojson \
    python3-google-auth-oauthlib \
    python3-gpiod \
    python3-gunicorn \
    python3-html2text \
    python3-httptools \
    python3-inflection \
    python3-inotify \
    python3-intervals \
    python3-ipy \
    python3-iso3166 \
    python3-jdcal \
    python3-jsmin \
    python3-msgpack \
    python3-multidict \
    python3-netaddr \
    python3-ordered-set \
    python3-parse \
    python3-parse-type \
    python3-platformdirs \
    python3-polyline \
    python3-portalocker \
    python3-precise-runner \
    python3-prettytable \
    python3-pydantic \
    python3-pydantic-core \
    python3-pylint \
    python3-ptyprocess \
    python3-py-cpuinfo \
    python3-pyasn1-modules \
    python3-pyroute2 \
    python3-pyserial \
    python3-pytest-mock \
    python3-pytoml \
    python3-pyyaml-include \
    python3-pydbus \
    python3-rapidjson \
    python3-requests-file \
    python3-requests-toolbelt \
    python3-semver \
    python3-serpent \
    python3-service-identity \
    python3-simpleeval \
    python3-smpplib \
    python3-soupsieve \
    python3-sqlparse \
    python3-tomli-w \
    python3-tomlkit \
    python3-trustme \
    python3-typeguard \
    python3-ujson \
    python3-u-msgpack-python \
    python3-unidiff \
    python3-uritemplate \
    python3-validators \
    python3-wrapt \
    python3-wsproto \
    python3-xlrd \
    python3-xmltodict \
    python3-xxhash \
    python3-yarl \
"

PTESTS_SLOW_META_PYTHON = "\
    python3-arrow \
    python3-ecdsa \
    python3-google-auth \
    python3-lz4 \
    python3-marshmallow \
    python3-pillow \
    python3-pytest-localserver \
    python3-scrypt \
    python3-traitlets \
    python3-yappi \
"

PTESTS_PROBLEMS_META_PYTHON ="\
    python3-betamax \
    python3-dnspython \
    python3-fastjsonschema \
    python3-pint \
    python3-pyzmq \
    python3-whoosh \
"
