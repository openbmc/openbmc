do_install_append_df-openpower () {

# The webui content is served as pre-compressed gzip content. While nginx can
# handle this via the gzip_static directive (and we use that), the nginx
# try_files directive (which we also use) searches the URI as-is. The suggested
# workaround is to have an empty uncompressed file with the same name as the
# compressed file. This does not impact the functionality of gzip_static.
# So for eg if there's an index.html.gz, create an empty index.html. Same goes
# for all the webui content.
# https://serverfault.com/questions/571733/nginx-gzip-static-why-are-the-non-compressed-files-required
# https://www.ruby-forum.com/topic/4402481
# https://trac.nginx.org/nginx/ticket/1367

find ${D}${datadir}/www -type f -name '*.gz' -exec sh -c 'touch `dirname "$0"`/`basename "$0" .gz`' '{}' \;

}
