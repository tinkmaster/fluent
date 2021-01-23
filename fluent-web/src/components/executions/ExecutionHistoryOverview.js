import React, {Component} from "react";
import {Card, Col, Row, Statistic, Empty, Popover, Alert} from 'antd';
import {timestampToDateTime} from '../../interfaces/Functions'
import './css/ExecutionHistoryOverview.css'

export class ExecutionHistoryOverview extends Component {

    constructor(props, context) {
        super(props, context);
    }

    render() {
        return (
            <div className="execution-overview">
                <Card
                    style={{height: '100%'}}
                    title={<span className="execution-overview-card-title">Execution Overview</span>}>
                    {this.props.executionOverview && this.props.executionOverview.totalExecutionNumber > 0 ?
                    <div>
                    <Row gutter={16}>
                        <Col span={8}>
                            <Statistic title="Total Execution Times" value={this.props.executionOverview.totalExecutionNumber}/>
                        </Col>
                        <Col span={8}>
                            <Statistic title="Latest Success" value={
                                this.props.executionOverview.lastSuccessfulExecution ? 
                                timestampToDateTime(new Date(this.props.executionOverview.lastSuccessfulExecution)) : 'Unknown'}/>
                        </Col>
                        <Col>
                            <Statistic title="Last Failure" value={this.props.executionOverview.lastFailedExecution ? 
                                timestampToDateTime(new Date(this.props.executionOverview.lastFailedExecution)) : 'Unknown'}/>
                        </Col>
                        
                    </Row>
                    <Row  gutter={16} style={{marginTop: 18}}>
                        <Col span={8}>
                            <Popover placement="topLeft" content={'Average Used Time Of All Successful Executions'}>
                                <Statistic title="Average Used Time" value={
                                    this.props.executionOverview.averageUsedTimeMs > 0 ? 
                                    this.props.executionOverview.averageUsedTimeMs : 'Unknown'
                                    }/>
                            </Popover>
                        </Col>
                    </Row>
                    <Row  gutter={16} style={{marginTop: 18, marginLeft: 1}}>
                        <h3 style={{color: 'green'}}>Execution Health overview</h3>
                    </Row>
                    <Row  gutter={16} style={{marginTop: 10}}>
                        <Col span={8}>
                            <Statistic title="Success" value={
                                this.props.executionOverview.successfulExecutionNumber
                                }/>
                        </Col>
                        <Col span={8}>
                            <Statistic title="Failure" value={
                                this.props.executionOverview.successfulExecutionNumber
                                }/>
                        </Col>
                        <Col span={8}>
                            <Statistic title="Running" value={
                                this.props.executionOverview.runningExecutionNumber
                                }/>
                        </Col>
                    </Row>
                    </div>
                    :
                        this.props.executionOverview ?
                        <Empty
                                description={
                                    <span>No Execution Records Found</span>
                                }></Empty>
                    : <Empty
                                description={
                                    <span>No Pipeline Selected</span>
                                }></Empty>}
                </Card>
            </div>
        )
    }

}
