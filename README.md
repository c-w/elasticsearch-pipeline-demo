[![Build Status](https://clewolff.visualstudio.com/elasticsearch-pipeline-demo/_apis/build/status/c-w.elasticsearch-pipeline-demo?branchName=master)](https://clewolff.visualstudio.com/elasticsearch-pipeline-demo/_build/latest?definitionId=6&branchName=master)

## Development setup

To get started, copy `.env.template` to `.env` and set the required configuration values.

Then, run the following snippet:

```bash
docker-compose up --build
```

The command will:

- Start a single-node ElasticSearch deployment.
- Set up an index and processing pipeline.
- Ingest sample documents.
- Convert formats such as DOCX or PDF to plain text.
- Analyze entities, key phrases and sentiment.

[![Overview of architecture](https://user-images.githubusercontent.com/1086421/59701575-ec436200-91c3-11e9-86c6-0b659a8bef87.png)](https://www.draw.io/?title=elasticsearch-pipeline-demo#R5Vtbd6o4FP41PuriKvqo9nraM9M1PXPm9OmsCBFSkbggttpfPwkGhCQiWrQ605eSLYRk5%2Fv2LaFljmbL2xjMg%2B%2FYg2HL0Lxly7xqGYbe62r0H5OsuKRrmmuJHyOPyzaCZ%2FQBuZA%2F6C%2BQB5PSjQTjkKB5WejiKIIuKclAHOP38m0THJbfOgc%2BlATPLghl6T%2FII8Fa2rO1jfwOIj%2FI3qxr%2FJcZyG7mgiQAHn4viMzrljmKMSbrq9lyBEOmvUwv6%2BdutvyaDyyGEanzANCSj%2B9Dc3B%2F5b8G5LaNRoNxu7fu5Q2ECz5hPliyyjQAI2%2FAFElbEY6ocOiBJICsV502AjIL%2BSVcIvKLXmsdm7de0pbW580rhgsta6wKjScYoxkkMOYyeXJ8vglexC4f2d3jt97r4Db4%2FeNZi6KHh2TlXrdtDhIQ%2B5BUzJz3B73S%2BnPV3UJMRxOv6A3vm1XPFj0oLHgmi2EICHorowZw8Pl5d%2FkbnjCiMzO0jCkm74fzxLa0chfrefOniqssdtQrd2T1hY7WipE6oheFaW9EKYjUgFKq35AAdR2ChCD3GYLYDVpGN6SaG45jeuWzq%2FvIg0sJdJQsc3bprkJEb6CwGL4HiMDnOUhX%2F52amzL4xnhBb%2FQex7kAuFM%2FZtI%2FF4T2Arl8K7TeYEzgshIM%2FFfLtktKNjOOF8Cia7aMlp62HRilldhX7fvx2KVLkiC3rMDDKWfJlKvCxplQrt8TFtE5kHJiR5Z%2BWsrpMue28mkxCwcuwYxPDO6IOrpHMIbhE04QQTiit4wxIXhGbwjZD8OcRSMcsudob%2BYk%2FSv0MQiRz54lWGAlXlNvlHvnSuNen4Fmz%2BmUtd7WuzIJTQV8usfioG5%2BnoRUI%2FGq4ENZ8yXzk6yxcaFpa7VLpTvJm6FnN3vPy2M6XZF19mH0dQT65gM8FX0tCTbPi%2FEMsSF62F3MYDpa2ocIJsoVUoZPDBP0AcbpDQwWczbodBr2sGVfUQngTHVprzBWUHiGPI89X4f%2BMuqqiSHSOw%2Fb%2BZBbxchYRfu21jHtrlFe%2BEbg1NZtVa9ZB3gySeBxAiZNWn4hYtLmaA7TAEay6u9oFoI0Jm%2FAptKwRjKgeThZZLDk4BozobIFpZ6DzaDMBYLZXEOAItZgLBA1E6%2BjQU6DHaFjTRjnK1XfS5Vjcd1ShInGKV2UbGoGEQhXNOmm2qOzR2sFizH69ZLEwGWan0I2vXkQg4Rm5dKNl7EOpnnCdVgFd8PXx%2B6vn8HH1bfX0dsfNy%2FtLPksrMMNJCnXCyifxDQMY9rEMStSXIRqVSbkpJqtEQvvjMGUZYzsly01jMbitsp1KUZzVcDaGcxxQ3smsVxeNMtCMNHB1K5%2BiB2JZZTmYjnofg%2Be7m2%2FN53e%2FW2Ph0%2Bv5KeC1xvTycwrQcxqXgKRDfOETFZOwfk8kz%2FBrirWFNlVBYMzYVe3L7g%2FsQpVO1OytvjRE7GrCdt%2BoJ0%2BxCd8An1VqNpp262zQt8RbbuQ8B85T7cl9A0%2BFjHMMxANsFCa5m2yhd9ZaisgVF1Sk9L2hMR4CrOknG%2FMTFAYFvJ0TRte3xjsZl79my19tk3XAWzgHTfEC%2B93AuM35DaUQraFwrityZG2Cmuim27MifQPNBkJBROpFyWWtrrsSouQWx%2B9ZHs2pqjx6p6iNO%2B8639NRv6D5T%2B%2BdafO6LfzQs6sNO9oAowO9Vj9r63MG4p4UKjtuDgiAEWU0yIuC8WdFI18b9wwG6r2GEL93JapatqKPSyrahOLv%2B4vVtOL%2FBBuf5%2BqXJ8XSIrvM8zy60BILWAECByyuDk5RqBRo5CfmVQ0Sw8QDNP%2Fg2S%2B2eUAWWOCliy6373hojb9%2FA1XASHszMOAzda4wXMYReG8A2imEMAOjn0qRDN%2F81M7xD7uzCN%2Fj1DkABRZZafcNzqKxEF3FDiyt8Oors1Xrl6NxOGrV4%2BgKSgvXSo59WIpvLN6qY7lnrOl%2BT9tXVqOuHVpOQrTe8pU25DrwvXCJHVm5ZRSK%2F1owU02ykvbujSFhDyvq%2Bwb3kgdiUcYjh3fyDlRVscGUYQJddJeq1jTFjB1KRuYOT%2FOcQOz0S3KquSgsMo%2FpIxXm8d4uaoMaD9Z59zb0Irne06561N15LBaj4VtuDPSZVuwNIZzwvKwUpm1QClsV56xQs3eVytUtuX1YoBDSyXGF9ZKqszchZ0cNrWGjjEa%2Bo6OmgsmlGrtnhh%2FzqXizzgv%2FImwObRWJxaIpI6OjD%2B5VPxM8UYlYxTSRNBnz0cTHM9Amm3yU0hpEb0KqP%2BlbybOC3n5zllWJc5OUu6LPLEj83g7S8pDG4dm3zUs3zGNWN0DKMvFahT8WnSN1x8D5%2F7t4X5%2BMz0zJyrlChfgRJW1z0a%2F5zrcoNQ9KOGcFQra4ldYUj2ltkURvsKSKjzHPockHyr%2BjI%2FKS3v7WZHMt%2Bklz1YddtXCXN3jEVWbO2eCOUsI3%2FVDwyfL7ld31Bzkqgy64nhEfoC12fRbUYyTYFO3NnTEY6u0ufnGd63szafS5vW%2F)

### Document Ingest Pipeline

The document ingest pipeline is specified in a json file and pulls together plugins with different functionality. In this project it is [injestion.json](devtool/ingestion.json). When a new document is ingested and the indexing command is run, the pipeline associated with that index is run.

#### OpenNLP Plugin 

The OpenNLP plugin can be easily extented with additional functionality, such as adding other models from the OpenNLP model bank or additional nlp processing. To do this first fork the original [repo](https://github.com/spinscale/elasticsearch-ingest-opennlp) and make the changes.

Once done then run `./gradlew clean check` to package the latest version of the plugin as a zip. This has to be referenced in the [Dockerfile](elasticsearch/Dockerfile) for the main elasticsearch project. [Reference docs](https://www.elastic.co/guide/en/elasticsearch/plugins/current/plugin-management-custom-url.html)

## Development links

- [ElasticSearch management UI](http://localhost:1358/?appname=devindex&url=http://localhost:9200)
- [ElasticSearch search UI](http://localhost:3000/?appname=devindex&url=http://localhost:9200)

## CI
Make sure the `.env` file is populated.

Then, run the following snippets:

```bash
docker build -t ci .
docker run -it -v /var/run/docker.sock:/var/run/docker.sock ci
```

This command will:

- Run all unit tests.
- Publish the built images to Azure Container Registry.
- Deploy the services to a new project in an OpenShift cluster.
- Run all integration tests.
- Delete the images on Azure Container Registry.
- Delete the project on the cluster.
