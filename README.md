# gRPC Stub Action

Build gRPC stub and upload to Github maven repository

## Usage

```yaml
steps:
- uses: actions/checkout@v3
- uses: belizwp/grpc-stub-action@main
  with:
    proto_dir: proto
    group: com.belizwp
    artifact_id: hello-world-api
    version: "0.0.1"
    gh_actor: ${{ secrets.GITHUB_ACTOR }}
    gh_token: ${{ secrets.GITHUB_TOKEN }}
```
