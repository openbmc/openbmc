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

inherit python_hatchling ptest-python-pytest

SRCREV = "cf67d4b3193c3fe43ede18612ed62785eee11382"
PV .= "+git"
SRC_URI = "git://github.com/pydantic/pydantic;protocol=https;branch=v2.13-fixes"
DEPENDS += "python3-hatch-fancy-pypi-readme-native"

CVE_PRODUCT = "pydantic:pydantic"

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
    python3-typing-inspection \
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
    python3-ruff \
    python3-pytest-examples \
    python3-devtools \
    python3-executing \
    python3-asttokens \
    ${PN}-doc \
"

do_install:append() {
   install -d ${D}${docdir}/${PN}/
   cp -rf ${S}/docs/* ${D}${docdir}/${PN}/
}

do_install_ptest:append() {
    cp -rf ${S}/tests/ ${D}${PTEST_PATH}/
    # Fix paths in test_docs.py
    sed -i \
        -e "s|^DOCS_ROOT = Path(__file__).parent.parent / 'docs'|DOCS_ROOT = Path('${docdir}/${PN}')|" \
        -e "s|^SOURCES_ROOT = Path(__file__).parent.parent / 'pydantic'|SOURCES_ROOT = Path('${PYTHON_SITEPACKAGES_DIR}/pydantic')|" \
        ${D}${PTEST_PATH}/tests/test_docs.py
    # We are not trying to support mypy
    rm -f ${D}${PTEST_PATH}/tests/test_mypy.py
    # We are not trying to run benchmarks
    rm -rf ${D}${PTEST_PATH}/tests/benchmarks
    # test_docs.py lint-checks every doc/docstring code example through ruff via
    # pytest-examples. ruff's rule set (import sorting, future-annotations,
    # datetime-tz, etc.) is not version-locked to what pydantic's examples were
    # written for, so the installed ruff flags ~130 style issues that are not
    # runtime bugs. Disable only the lint step; the examples are still executed.
    sed -i "s/^    if not lint_settings.startswith('skip'):/    if False:  # OE: skip ruff lint (uncontrolled ruff version -> lint-only failures)/" \
        ${D}${PTEST_PATH}/tests/test_docs.py
    # test_deprecated_fields.py uses pytest.warns(Warning, callable, match=...);
    # newer pytest forwards match= to the callable instead of consuming it, so
    # these raise "unexpected keyword argument 'match'". pytest API version skew.
    sed -i -e "/--automake/ s/$/ -k 'not test_config_validation_error_cause and not test_dataclass_config_validate_default and not test_annotated_validator_nested and not test_use_bare and not test_use_no_fields and not test_validator_bad_fields_throws_configerror and not test_assert_raises_validation_error and not test_model_config_validate_default and not test_readonly_qualifier_warning and not test_deprecated_fields and not test_deprecated_fields_deprecated_class and not test_computed_field_deprecated and not test_deprecated_with_boolean and not test_deprecated_field_forward_annotation'/" ${D}${PTEST_PATH}/run-ptest
}

FILES:${PN}-doc += "${docdir}/${PN}"

BBCLASSEXTEND = "native nativesdk"
