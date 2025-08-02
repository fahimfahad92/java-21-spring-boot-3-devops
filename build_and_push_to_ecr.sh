#!/bin/bash

# update below properties to build and push Docker image to ECR
docker_file=Dockerfile-liberica-multi-stage
image_name=demo-app-liberica-multi-stage
aws_account_id=<account-id>
aws_region=ap-southeast-1
aws_ecr_repository_name=fahim/demo-app-liberica

echo 'Starting docker build'
docker build . -t $image_name -f  $docker_file
echo 'Completed docker build'

echo 'Creating docker tag'
docker tag $image_name:latest $aws_account_id.dkr.ecr.$aws_region.amazonaws.com/$aws_ecr_repository_name
echo 'Docker tag created'

echo 'Login to ECR'
aws ecr get-login-password --region $aws_region | docker login --username AWS --password-stdin $aws_account_id.dkr.ecr.$aws_region.amazonaws.com


echo 'Pushing docker image to ECR'
docker push $aws_account_id.dkr.ecr.$aws_region.amazonaws.com/$aws_ecr_repository_name
echo 'Docker image pushing to ECR completed'

echo 'Exiting script...'

sleep 10