Make sure the `.env` file is populated.

Then, run the following snippets:

```bash
docker build -t cleanup .
docker run -it -v /var/run/docker.sock:/var/run/docker.sock cleanup
```
