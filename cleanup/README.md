# About
When PRs are made, the CI pushes images to ACR, deploys a new project to the OpenShift cluster, and runs integration tests. When the CI exits, the images are deleted from ACR. The project is not deleted, which makes it easy to demo the PR's changes.

Once the PR has been successfully merged, we would like to delete the project deployed by the CI.

# Instructions
Executing the following instructions will:
- Create a new project, `cleanup`, on the cluster.
- Create a service that will cleanup resources when called.
- Expose a URL for the service that can be called via a webhook in AZDO upon successful merge of a PR.
- Print the webhook URL to add in AZDO

Instructions:
In the current directory, copy `.env.template` to `.env` and set the required configuration values.
In the `server` directory, copy `.env.template` to `.env` and set the required configuration values.

Run the following snippets:

```bash
docker build -t cleanup .
docker run -it -v /var/run/docker.sock:/var/run/docker.sock cleanup
```
