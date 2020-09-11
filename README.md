# Different build example for Quarkus on OpenShift

This is a small quarkus demo project that can be used to deploy to openshift

## Build and Deploy a Quarkus application using Java S2I

    oc new-project demo-jvm
    
    oc new-app registry.access.redhat.com/ubi8/openjdk-11~https://github.com/tqvarnst/q-app
    
    oc expose svc q-app

This will checkout this project into a container and build a JAR file and create a runnable container. The new-app command will also create a service and deployment configurations for us.

## Build and Deploy a native Quarkus application using S2I

To deploy this application as a native build using the Quarkus S2I image do the following

    oc new-project demo-s2i-native

    oc import-image quarkus/ubi-quarkus-native-s2i --from=quay.io/quarkus/ubi-quarkus-native-s2i:20.1.0-java11 --confirm

    oc new-app --name=q-app quay.io/quarkus/ubi-quarkus-native-s2i:20.1.0-java11~https://github.com/tqvarnst/q-app.git

    # wait for the build to finish (web console or `oc logs -f bc/q-app`)

    oc expose svc q-app

> **NOTE:** GraalVM are using a lot of memory during build which may cause issues depending on your OpenShift clusters limitations. To increase the limits you can do the follow:
>
> First stop the build (if running)
>
>       oc cancel-build bc/q-app  
> Then increase the memory of the pod to a suitable value
>
>       oc patch bc/q-app -p '{"spec":{"resources":{"limits":{"cpu":"4", "memory":"4Gi"}}}}'
> Then start the build again
>
>       oc start-build q-app


## Deploy as minimal runtime container

In the Deploy as S2I example the container that builds the native application is also running the application. This means that the image contains both OpenJDK, GraalVM, Maven, etc even though that is not required to run the application. 

An alternative way of deploying this is to use a chained build process where we have one build process to build the native application and another one that creates a minimal container that only consists of the base container and the native application it self. 

The following commands will create a chained build.

    oc new-project demo-minimal-native

    oc import-image quarkus/ubi-quarkus-native-s2i --from=quay.io/quarkus/ubi-quarkus-native-s2i:20.1.0-java11 --confirm

    oc new-build --name=q-app-build quay.io/quarkus/ubi-quarkus-native-s2i:20.1.0-java11~https://github.com/tqvarnst/q-app.git 

    oc new-build --name=q-app \
        --docker-image=registry.access.redhat.com/ubi7-dev-preview/ubi-minimal \
        --source-image=q-app-build \
        --source-image-path='/home/quarkus/application:.' \
        --dockerfile=$'FROM registry.access.redhat.com/ubi8/ubi-minimal:latest\nCOPY application /application\nCMD /application -Xmx8M -Xms8M -Xmn8M\nEXPOSE 8080' \
        --allow-missing-imagestream-tags

    # wait for the build to finish (web console or `oc logs -f bc/q-app`)

    oc new-app q-app

    oc expose svc q-app

> **NOTE:** GraalVM are using a lot of memory during build which may cause issues depending on your OpenShift clusters limitations. To increase the limits you can do the follow:
>
> First stop the build (if running)
>
>       oc cancel-build bc/q-app  
> Then increase the memory of the pod to a suitable value
>
>       oc patch bc/q-app -p '{"spec":{"resources":{"limits":{"cpu":"4", "memory":"4Gi"}}}}'
> Then start the build again
>
>       oc start-build q-app
