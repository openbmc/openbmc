METADATA_BRANCH ?= "${@base_detect_branch(d)}"
METADATA_REVISION ?= "${@base_detect_revision(d)}"

def base_detect_revision(d):
    path = base_get_scmbasepath(d)

    scms = [base_get_metadata_git_revision]

    for scm in scms:
        rev = scm(path, d)
        if rev != "<unknown>":
            return rev

    return "<unknown>"

def base_detect_branch(d):
    path = base_get_scmbasepath(d)

    scms = [base_get_metadata_git_branch]

    for scm in scms:
        rev = scm(path, d)
        if rev != "<unknown>":
            return rev.strip()

    return "<unknown>"

def base_get_scmbasepath(d):
    return os.path.join(d.getVar('COREBASE', True), 'meta')

def base_get_metadata_monotone_branch(path, d):
    monotone_branch = "<unknown>"
    try:
        with open("%s/_MTN/options" % path) as f:
            monotone_branch = f.read().strip()
            if monotone_branch.startswith( "database" ):
                monotone_branch_words = monotone_branch.split()
                monotone_branch = monotone_branch_words[ monotone_branch_words.index( "branch" )+1][1:-1]
    except:
        pass
    return monotone_branch

def base_get_metadata_monotone_revision(path, d):
    monotone_revision = "<unknown>"
    try:
        with open("%s/_MTN/revision" % path) as f:
            monotone_revision = f.read().strip()
            if monotone_revision.startswith( "format_version" ):
                monotone_revision_words = monotone_revision.split()
                monotone_revision = monotone_revision_words[ monotone_revision_words.index( "old_revision" )+1][1:-1]
    except IOError:
        pass
    return monotone_revision

def base_get_metadata_svn_revision(path, d):
    # This only works with older subversion. For newer versions 
    # this function will need to be fixed by someone interested
    revision = "<unknown>"
    try:
        with open("%s/.svn/entries" % path) as f:
            revision = f.readlines()[3].strip()
    except (IOError, IndexError):
        pass
    return revision

def base_get_metadata_git_branch(path, d):
    import bb.process

    try:
        rev, _ = bb.process.run('git rev-parse --abbrev-ref HEAD', cwd=path)
    except bb.process.ExecutionError:
        rev = '<unknown>'
    return rev.strip()

def base_get_metadata_git_revision(path, d):
    import bb.process

    try:
        rev, _ = bb.process.run('git rev-parse HEAD', cwd=path)
    except bb.process.ExecutionError:
        rev = '<unknown>'
    return rev.strip()
