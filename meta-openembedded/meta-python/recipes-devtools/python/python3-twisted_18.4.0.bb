inherit pypi setuptools3
require python-twisted.inc

PACKAGES_remove = "${PN}-src"

FILES_${PN}-core_append += " \
  ${libdir}/${PYTHON_DIR}/site-packages/twisted/__pycache__ \
  ${libdir}/${PYTHON_DIR}/site-packages/twisted/python/__pycache__/*pyc \
  ${libdir}/${PYTHON_DIR}/site-packages/twisted/plugins/__pycache__/__init__*.pyc \
  ${libdir}/${PYTHON_DIR}/site-packages/twisted/plugins/__pycache__/notestplugin*.pyc \
  ${libdir}/${PYTHON_DIR}/site-packages/twisted/plugins/__pycache__/testplugin*.pyc \
  ${libdir}/${PYTHON_DIR}/site-packages/twisted/plugins/__pycache__/twisted_ftp*.pyc \
  ${libdir}/${PYTHON_DIR}/site-packages/twisted/plugins/__pycache__/twisted_inet*.pyc \
  ${libdir}/${PYTHON_DIR}/site-packages/twisted/plugins/__pycache__/twisted_manhole*.pyc \
  ${libdir}/${PYTHON_DIR}/site-packages/twisted/plugins/__pycache__/twisted_portforward*.pyc \
  ${libdir}/${PYTHON_DIR}/site-packages/twisted/plugins/__pycache__/twisted_socks*.pyc \
  ${libdir}/${PYTHON_DIR}/site-packages/twisted/plugins/__pycache__/twisted_telnet*.pyc \
  ${libdir}/${PYTHON_DIR}/site-packages/twisted/plugins/__pycache__/twisted_trial*.pyc \
  ${libdir}/${PYTHON_DIR}/site-packages/twisted/plugins/__pycache__/twisted_core*.pyc \
  ${libdir}/${PYTHON_DIR}/site-packages/twisted/plugins/__pycache__/twisted_qtstub*.pyc \
  ${libdir}/${PYTHON_DIR}/site-packages/twisted/plugins/__pycache__/twisted_reactors*.pyc \
  ${libdir}/${PYTHON_DIR}/site-packages/twisted/plugins/__pycache__/cred*.pyc \
  ${libdir}/${PYTHON_DIR}/site-packages/twisted/plugins/__pycache__/dropin*.cache \
"

FILES_${PN}-names_append = " \
  ${libdir}/${PYTHON_DIR}/site-packages/twisted/plugins/__pycache__/twisted_names*.pyc \
"

FILES_${PN}-news_append = " \
  ${libdir}/${PYTHON_DIR}/site-packages/twisted/plugins/__pycache__/twisted_news*.pyc \
"

FILES_${PN}-protocols_append += " \
  ${libdir}/${PYTHON_DIR}/site-packages/twisted/protocols/__pycache__/*pyc \
"

FILES_${PN}-conch_append = " \
  ${libdir}/${PYTHON_DIR}/site-packages/twisted/plugins/__pycache__/twisted_conch*.pyc \
"

FILES_${PN}-lore_append = " \
  ${libdir}/${PYTHON_DIR}/site-packages/twisted/plugins/__pycache__/twisted_lore*.pyc \
"
FILES_${PN}-mail_append = " \
  ${libdir}/${PYTHON_DIR}/site-packages/twisted/plugins/__pycache__/twisted_mail*.pyc \
"

FILES_${PN}-web_append = " \
  ${libdir}/${PYTHON_DIR}/site-packages/twisted/plugins/__pycache__/twisted_web*.pyc \
"

FILES_${PN}-words_append = " \
  ${libdir}/${PYTHON_DIR}/site-packages/twisted/plugins/__pycache__/twisted_words*.pyc \
"

FILES_${PN}-flow_append = " \
  ${libdir}/${PYTHON_DIR}/site-packages/twisted/plugins/__pycache__/twisted_flow*.pyc \
"

FILES_${PN}-pair_append = " \
  ${libdir}/${PYTHON_DIR}/site-packages/twisted/plugins/__pycache__/twisted_pair*.pyc \
"

FILES_${PN}-runner_append = " \
  ${libdir}/${PYTHON_DIR}/site-packages/twisted/plugins/__pycache__/twisted_runner*.pyc \
"
