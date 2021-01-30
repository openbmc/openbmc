METADATA_BRANCH ?= "${@base_detect_branch(d)}"
METADATA_BRANCH[vardepvalue] = "${METADATA_BRANCH}"
METADATA_REVISION ?= "${@base_detect_revision(d)}"
METADATA_REVISION[vardepvalue] = "${METADATA_REVISION}"

def base_detect_revision(d):
    path = base_get_scmbasepath(d)
    return base_get_metadata_git_revision(path, d)

def base_detect_branch(d):
    path = base_get_scmbasepath(d)
    return base_get_metadata_git_branch(path, d)

def base_get_scmbasepath(d):
    return os.path.join(d.getVar('COREBASE'), 'meta')

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
