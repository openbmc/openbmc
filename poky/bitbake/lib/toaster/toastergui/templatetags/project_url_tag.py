#
# SPDX-License-Identifier: GPL-2.0-only
#

from django import template
from django.core.urlresolvers import reverse

register = template.Library()

def project_url(parser, token):
    """
    Create a URL for a project's main page;
    for non-default projects, this is the configuration page;
    for the default project, this is the project builds page
    """
    try:
        tag_name, project = token.split_contents()
    except ValueError:
        raise template.TemplateSyntaxError(
            "%s tag requires exactly one argument" % tag_name
        )
    return ProjectUrlNode(project)

class ProjectUrlNode(template.Node):
    def __init__(self, project):
        self.project = template.Variable(project)

    def render(self, context):
        try:
            project = self.project.resolve(context)
            if project.is_default:
                return reverse('projectbuilds', args=(project.id,))
            else:
                return reverse('project', args=(project.id,))
        except template.VariableDoesNotExist:
            return ''

register.tag('project_url', project_url)
