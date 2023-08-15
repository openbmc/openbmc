#!/bin/sh
# Convert OpenBMC linux-PAM config files

# Location of config files this script modifies:
#   PAM_CONF_DIR - path to the PAM config files
#   SECURITY_CONF_DIR - path to the security config files
PAM_CONF_DIR=/etc/pam.d
SECURITY_CONF_DIR=/etc/security

# Handle common-password:
#   Change cracklib to pwquality and handle the minlen parameter
pam_cracklib=$(grep "^password.*pam_cracklib.so" ${PAM_CONF_DIR}/common-password)
if [ -n "${pam_cracklib}" ]
then
    echo "Changing ${PAM_CONF_DIR}/common-password to use pam_pwquality.so (was pam_cracklib.so)" >&2
    minlen=$(echo "${pam_cracklib}" | sed -e "s/.*minlen=\([[:alnum:]]*\).*/\1/")
    echo "  Converting parameter minlen=${minlen} to ${SECURITY_CONF_DIR}/pwquality.conf minlen" >&2
    sed -i.bak -e "s/^minlen=.*/minlen=$minlen/" ${SECURITY_CONF_DIR}/pwquality.conf
    pwquality='password        [success=ok default=die]        pam_pwquality.so debug'
    sed -i.bak -e "s/^password.*pam_cracklib.so.*/$pwquality/" ${PAM_CONF_DIR}/common-password
    echo "# This file was converted by $0" >>${PAM_CONF_DIR}/common-password
fi

#   Update pwhistory to use the conf file and handle the remember parameter
pam_pwhistory=$(grep "^password.*pam_pwhistory.so.*remember" ${PAM_CONF_DIR}/common-password)
if [ -n "${pam_pwhistory}" ]
then
    echo "Changing ${PAM_CONF_DIR}/common-password pam_pwhistory.so to use pwhistory.conf" >&2
    remember=$(echo "${pam_pwhistory}" | sed -e "s/.*remember=\([[:alnum:]]*\).*/\1/")
    echo "  Converting parameter remember=${remember} to ${SECURITY_CONF_DIR}/pwhistory.conf remember" >&2
    sed -i.bak -e "s/^remember=.*/remember=$remember/" ${SECURITY_CONF_DIR}/pwhistory.conf
    pwhistory='password        [success=ok ignore=ignore default=die]  pam_pwhistory.so debug use_authtok'
    sed -i.bak -e "s/^password.*pam_pwhistory.so.*/$pwhistory/" ${PAM_CONF_DIR}/common-password
    echo "# This file was converted by $0" >>${PAM_CONF_DIR}/common-password
fi

# Handle common-auth:
#   Change tally2 to faillock and handle the deny & unlock_time parameters
pam_tally2=$(grep "^auth.*pam_tally2.so" ${PAM_CONF_DIR}/common-auth)
if [ -n "${pam_tally2}" ]
then
    echo "Changing ${PAM_CONF_DIR}/common-auth to use pam_faillock.so (was pam_tally2.so)" >&2
    deny=$(echo "${pam_tally2}" | sed -e "s/.*deny=\([[:alnum:]]*\).*/\1/")
    unlock_time=$(echo "${pam_tally2}" | sed -e "s/.*unlock_time=\([[:alnum:]]*\).*/\1/")
    # Change faillock.conf parameters
    echo "  Converting parameter deny=${deny} to ${SECURITY_CONF_DIR}/faillock.conf deny" >&2
    echo "  Converting parameter unlock_time=${unlock_time} to ${SECURITY_CONF_DIR}/faillock.conf unlock_time" >&2
    sed -i.bak \
        -e "s/^deny=.*/deny=$deny/" \
        -e "s/^unlock_time=.*/unlock_time=$unlock_time/" \
        ${SECURITY_CONF_DIR}/faillock.conf
    # Change pam_tally2 to pam_faillock (changes the overall auth stack)
    authfail='auth    [default=die]                   pam_faillock.so authfail'
    authsucc='auth    sufficient                      pam_faillock.so authsucc'
    sed -i.bak \
        -e "/^auth.*pam_tally2.so.*$/d" \
        -e "/^auth.*pam_deny.so/i $authfail\n$authsucc" \
        ${PAM_CONF_DIR}/common-auth
    echo "# This file was converted by $0" >>${PAM_CONF_DIR}/common-auth
fi

