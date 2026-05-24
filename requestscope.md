sequenceDiagram
    autonumber
    actor Operator as Operator / CI-CD
    participant Repo as Config Repo (ConfigMap)
    participant Server as Config Server Pod
    participant Actuator as Order Service (/actuator/refresh)
    participant Context as Spring Application Context
    participant Controller as OrderController (Proxy)
    actor Client as End User

    Note over Controller: Initial State: "Default Message" loaded & cached
    Operator->>Repo: 1. Modify "app.dynamic.message" in order-service.yml
    Operator->>Repo: 2. Update Kubernetes ConfigMap
    Repo-->>Server: Files update on disk (after kubelet sync)
    
    Operator->>Actuator: 3. Trigger POST /actuator/refresh
    Actuator->>Server: 4. Fetch latest properties
    Server-->>Actuator: Return updated order-service.yml
    
    Actuator->>Context: 5. Update Spring Environment
    Actuator->>Context: 6. Evict/Clear cached @RefreshScope beans
    Context-->>Operator: 7. Return list of changed keys ["app.dynamic.message"]

    Note over Controller: Controller cache is empty / cleared
    
    Client->>Controller: 8. GET /api/orders/config-message
    Controller->>Context: 9. Interceptor detects empty cache (Request target bean)
    Context->>Context: 10. Instantiate new OrderController bean instance
    Note over Context: Inject updated value from Environment into @Value
    Context-->>Controller: Cache new bean instance
    Controller-->>Client: 11. Return updated configuration message!
