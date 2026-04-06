FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

# Audit configuration variables can be overridden in distro config or local.conf
# No changes results in using package defaults

# Configuration variables for audit rules
AUDIT_EXCLUDE_BPF ??= "0"

# Configuration variables for auditd daemon
# Defaults come from Linux audit upstream layer if not overridden
AUDITD_MAX_LOG_FILE ??= ""
AUDITD_NUM_LOGS ??= ""
AUDITD_MAX_LOG_FILE_ACTION ??= ""

# Configuration variables for logging audit entries
AUDIT_JOURNAL ??= "1"

do_compile:append() {
    # ========================================================================
    # Generate openbmc_audit.rules with custom rules
    # ========================================================================
    cat > ${WORKDIR}/openbmc_audit.rules << 'EOF'
# OpenBMC Default Audit Rules
# Generated dynamically based on build configuration
# DO NOT EDIT - This file is auto-generated during build

EOF

    # Add BPF exclusion rule if enabled
    if [ "${AUDIT_EXCLUDE_BPF}" = "1" ]; then
        cat >> ${WORKDIR}/openbmc_audit.rules << 'EOF'
## Exclude BPF messages to reduce log noise
-a always,exclude -F msgtype=BPF

EOF
    fi
}

do_install:append() {
    # Install dynamically generated audit rules
    install -d ${D}${sysconfdir}/audit/rules.d
    install -m 0640 ${WORKDIR}/openbmc_audit.rules \
        ${D}${sysconfdir}/audit/rules.d/openbmc_audit.rules

    # ========================================================================
    # Modify auditd.conf with custom settings (if specified)
    # ========================================================================

    if [ -f "${D}${sysconfdir}/audit/auditd.conf" ]; then
        # Modify max_log_file if specified
        if [ -n "${AUDITD_MAX_LOG_FILE}" ]; then
            sed -i "s/^max_log_file =.*/max_log_file = ${AUDITD_MAX_LOG_FILE}/" \
                ${D}${sysconfdir}/audit/auditd.conf
        fi

        # Modify num_logs if specified
        if [ -n "${AUDITD_NUM_LOGS}" ]; then
            sed -i "s/^num_logs =.*/num_logs = ${AUDITD_NUM_LOGS}/" \
                ${D}${sysconfdir}/audit/auditd.conf
        fi

        # Modify max_log_file_action if specified
        if [ -n "${AUDITD_MAX_LOG_FILE_ACTION}" ]; then
            sed -i "s/^max_log_file_action =.*/max_log_file_action = ${AUDITD_MAX_LOG_FILE_ACTION}/" \
                ${D}${sysconfdir}/audit/auditd.conf
        fi
    fi
}

# ========================================================================
# Modify systemd with custom settings (if specified)
# ========================================================================
PACKAGE_WRITE_DEPS:auditd:append = " ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'systemd-systemctl-native', '',d)}"

pkg_postinst:auditd:append () {
    if [ "${AUDIT_JOURNAL}" = "0" ]; then
        if ${@bb.utils.contains('DISTRO_FEATURES', 'systemd','true','false',d)}; then
            # Turn off audit entries in journal
            systemctl --root=$D mask systemd-journald-audit.socket
        fi
    fi
}

