import React from 'react';
import {Card, Descriptions, Empty, Popover} from "antd";
import ReadOutlined from "@ant-design/icons/lib/icons/ReadOutlined";
import './css/ExecutionNodeInfo.css'

export class ExecutionNodeInfo extends React.Component {

    constructor(props, context) {
        super(props, context);
    }

    render() {

        let nodeResult;
        if (this.props.diagram.result[this.props.node.id]) {
            nodeResult = JSON.parse(this.props.diagram.result[this.props.node.id]);
        }
        return (
            <div className={'execution-node-info'}>
                <Card
                    title={<span className="execution-node-info-card-title">Execution Node Info</span>}>
                    <Descriptions>
                        <Descriptions.Item label="Operator Name">{this.props.node.operator.name}</Descriptions.Item>
                        <Descriptions.Item label="Operator Type">{this.props.node.operator.type}</Descriptions.Item>
                        <Descriptions.Item
                            label="Used time (ms)">{this.props.node.usedTime ? this.props.node.usedTime : 'Unknown'}</Descriptions.Item>
                        <Descriptions.Item label="Status" span={3}>{this.props.node.status}</Descriptions.Item>
                    </Descriptions>
                    {
                        this.props.node.status === 'FAILED' ?
                            <Descriptions title={'Error Info'}>
                                <Descriptions.Item
                                    label="Reason"><span
                                    style={{fontWeight: 'bold'}}>{nodeResult.error}</span></Descriptions.Item>
                            </Descriptions> : ''
                    }

                    {
                        this.props.node.operator.type === 'HTTP_REQUEST' ?
                            <div>
                                {this.props.node.status === 'FINISHED' ?
                                    <Descriptions title={'Http Response'}>
                                        <Descriptions.Item
                                            label="Response Code">{nodeResult.status.code}</Descriptions.Item>
                                        <Descriptions.Item
                                            label="Response Headers"><Popover content={
                                            <pre>{nodeResult.headers && Object.keys(nodeResult.headers).length !== 0 ?
                                                this.objectToString(nodeResult.headers) : <Empty/>}</pre>}
                                                                              trigger="hover">
                                            <ReadOutlined style={{marginTop: 'auto', marginBottom: 'auto'}}/>
                                        </Popover></Descriptions.Item>
                                        <Descriptions.Item
                                            label="Response Body"><Popover style={{
                                            backgroundColor: '#b36666'
                                        }} content={
                                            <pre>{nodeResult.headers && Object.keys(nodeResult.headers).length !== 0 ?
                                                this.formatJson(nodeResult.body) : <Empty/>}</pre>}
                                                                           trigger="hover">
                                            <ReadOutlined style={{marginTop: 'auto', marginBottom: 'auto'}}/>
                                        </Popover></Descriptions.Item>
                                    </Descriptions> : ''}
                                <Descriptions title={'Http Request'}>
                                    <Descriptions.Item
                                        label="Http HOST">{this.props.node.operator.params.uri}</Descriptions.Item>
                                    <Descriptions.Item
                                        label="Http Path">{this.props.node.operator.params.path}</Descriptions.Item>
                                    <Descriptions.Item
                                        label="Http Method">{this.props.node.operator.params.method}</Descriptions.Item>
                                    <Descriptions.Item
                                        label="Http Headers">
                                        <Popover content={
                                            <pre>{this.props.node.operator.params.headers
                                            && this.props.node.operator.params.headers.trim().length !== 0 ?
                                                this.props.node.operator.params.headers : <Empty/>}</pre>}
                                                 trigger="hover">
                                            <ReadOutlined style={{marginTop: 'auto', marginBottom: 'auto'}}/>
                                        </Popover>
                                    </Descriptions.Item>
                                    <Descriptions.Item
                                        label="Http Request Body">
                                        <Popover content={
                                            <pre>{this.props.node.operator.params.body
                                            && this.props.node.operator.params.body.trim().length !== 0 ?
                                                this.props.node.operator.params.body : <Empty/>}</pre>}
                                                 trigger="hover">
                                            <ReadOutlined style={{marginTop: 'auto', marginBottom: 'auto'}}/>
                                        </Popover>
                                        {}
                                    </Descriptions.Item>
                                </Descriptions>
                            </div>
                            : ''}
                </Card>


            </div>
        )
    }


    objectToString(obj) {
        let str = ''
        const keys = Object.keys(obj)
        for (let i = 0; i < keys.length; i++) {
            if (i !== 0) {
                str = str + '\n'
            }
            str = str + keys[i] + ': ' + obj[keys[i]]
        }
        return str;
    }

    formatJson(json) {
        var i = 0,
            len = 0,
            tab = "    ",
            targetJson = "",
            indentLevel = 0,
            inString = false,
            currentChar = null;


        for (i = 0, len = json.length; i < len; i += 1) {
            currentChar = json.charAt(i);

            switch (currentChar) {
                case '{':
                case '[':
                    if (!inString) {
                        targetJson += currentChar + "\n" + this.repeat(tab, indentLevel + 1);
                        indentLevel += 1;
                    } else {
                        targetJson += currentChar;
                    }
                    break;
                case '}':
                case ']':
                    if (!inString) {
                        indentLevel -= 1;
                        targetJson += "\n" + this.repeat(tab, indentLevel) + currentChar;
                    } else {
                        targetJson += currentChar;
                    }
                    break;
                case ',':
                    if (!inString) {
                        targetJson += ",\n" + this.repeat(tab, indentLevel);
                    } else {
                        targetJson += currentChar;
                    }
                    break;
                case ':':
                    if (!inString) {
                        targetJson += ": ";
                    } else {
                        targetJson += currentChar;
                    }
                    break;
                case ' ':
                case "\n":
                case "\t":
                    if (inString) {
                        targetJson += currentChar;
                    }
                    break;
                case '"':
                    if (i > 0 && json.charAt(i - 1) !== '\\') {
                        inString = !inString;
                    }
                    targetJson += currentChar;
                    break;
                default:
                    targetJson += currentChar;
                    break;
            }
        }
        return targetJson;
    }

    repeat(s, count) {
        return new Array(count + 1).join(s);
    }

}
