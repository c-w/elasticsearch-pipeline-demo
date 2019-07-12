import hug
from kubernetes import client, config
from openshift.dynamic import DynamicClient
from openshift.dynamic.exceptions import NotFoundError


def _delete_project(project_name):
    k8s_client = config.new_client_from_config()
    dyn_client = DynamicClient(k8s_client)
    projects = dyn_client.resources.get(
        api_version="project.openshift.io/v1", kind="Project"
    )
    try:
        print(f"attempting to delete {project_name}...")
        print(projects.delete(name=project_name))
    except NotFoundError:
        print(f"{project_name} was not found.")


@hug.post("/")
def cleanup_project(body):
    """Cleanup cluster resources upon PR close."""
    if body.get("action") == "closed":
        pr_id = body.get("pull_request", {}).get("number")
        if pr_id is not None:
            _delete_project(f"ci-refs-pull-{pr_id}-merge")
