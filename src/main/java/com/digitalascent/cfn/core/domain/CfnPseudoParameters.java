package com.digitalascent.cfn.core.domain;

@SuppressWarnings("unused")
interface CfnPseudoParameters {
    default String awsAccountId() { return "AWS::AccountId"; }

    default String awsNotificationArns() { return "AWS::NotificationARNs"; }

    default String awsNoValue() { return "AWS::NoValue"; }

    default String awsRegion() { return "AWS::Region"; }

    default String awsStackId() { return "AWS::StackId"; }

    default String awsStackName() { return "AWS::StackName"; }

}
