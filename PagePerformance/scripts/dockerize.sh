#!/bin/bash
cd ..
mvn package
cp ./target/PagePerformanceCacher-0.0.1-SNAPSHOT.jar ../docker/site-optimization/docker/images/pageperformance_cacher/
cd ./../docker/site-optimization/
docker-compose -f docker-compose-pageperformance-cacher.yml build
docker push ryanhyma/pageperformance_cacher:1.0.0
ls
