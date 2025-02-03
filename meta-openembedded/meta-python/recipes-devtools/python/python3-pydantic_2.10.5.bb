SUMMARY = "Data validation using Python type hinting"
DESCRIPTION = "Data validation and settings management using Python \
type hints.\
\
Fast and extensible, Pydantic plays nicely with your linters/IDE/brain. \
Define how data should be in pure, canonical Python 3.7+; validate it with \
Pydantic."
HOMEPAGE = "https://github.com/samuelcolvin/pydantic"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=09280955509d1c4ca14bae02f21d49a6"

inherit pypi python_hatchling ptest-python-pytest

SRC_URI[sha256sum] = "278b38dbbaec562011d659ee05f63346951b3a248a6f3642e1bc68894ea2b4ff" 

DEPENDS += "python3-hatch-fancy-pypi-readme-native"

RECIPE_NO_UPDATE_REASON = "Must be updated in sync with python3-pydantic-core."

RDEPENDS:${PN} += "\
    python3-annotated-types \
    python3-core \
    python3-datetime \
    python3-image \
    python3-io \
    python3-json \
    python3-jsonschema \
    python3-logging \
    python3-netclient \
    python3-numbers \
    python3-profile \
    python3-pydantic-core \
    python3-typing-extensions \
    python3-tzdata \
    python3-zoneinfo \
"

RDEPENDS:${PN}-ptest += "\
    python3-ansi2html \
    python3-coverage \
    python3-cloudpickle \
    python3-dirty-equals \
    python3-email-validator \
    python3-fastjsonschema \
    python3-greenlet \
    python3-html \
    python3-hypothesis \
    python3-mypy \
    python3-packaging \
    python3-pydoc \
    python3-pytest-codspeed \
    python3-pytest-mock \
    python3-pytz \
    python3-rich \
    python3-sqlalchemy \
    python3-unixadmin \
"

do_install_ptest:append() {
    cp -rf ${S}/tests/ ${D}${PTEST_PATH}/
    # Requires 'ruff' (python3-ruff) which we cannot build
    # until we have Rust 1.71+ in oe-core
    rm -f ${D}${PTEST_PATH}/tests/test_docs.py
    # We are not trying to support mypy
    rm -f ${D}${PTEST_PATH}/tests/test_mypy.py
    # We are not trying to run benchmarks
    rm -rf ${D}${PTEST_PATH}/tests/benchmarks
    sed -i -e "/--automake/ s/$/ -k 'not test_config_validation_error_cause and not test_dataclass_config_validate_default and not test_annotated_validator_nested and not test_use_bare and not test_use_no_fields and not test_validator_bad_fields_throws_configerror and not test_assert_raises_validation_error and not test_model_config_validate_default'/" ${D}${PTEST_PATH}/run-ptest
}

BBCLASSEXTEND = "native nativesdk"
