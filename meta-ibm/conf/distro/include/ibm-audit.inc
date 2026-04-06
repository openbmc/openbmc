# Enable audit with default configuration for IBM systems
# This file enables audit and sets default rules/daemon configuration

# Include core audit infrastructure
require conf/distro/include/linux-audit.inc

# ============================================================================
# AUDIT RULES CONFIGURATION
# ============================================================================
#
# RATIONALE FOR RULE SELECTION:
# ------------------------------
# AUDIT_EXCLUDE_BPF: Reduce noise from BPF events. BPF (Berkeley Packet
#    Filter) events are very frequent in modern systems and typically not
#    security-relevant for BMC monitoring

# Exclude noisy message types
AUDIT_EXCLUDE_BPF = "1"

# ============================================================================
# AUDITD DAEMON CONFIGURATION
# ============================================================================

# Limit amount of disk space logging can consume
AUDITD_MAX_LOG_FILE = "2"
AUDITD_NUM_LOGS = "5"
AUDITD_MAX_LOG_FILE_ACTION = "ROTATE"

# ============================================================================
# SYSTEM DAEMON CONFIGURATION
# ============================================================================

# Disable writing audit entries to journal
AUDIT_JOURNAL = "0"
