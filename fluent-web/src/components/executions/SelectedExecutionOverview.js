import React, {Component} from "react";
import {Card, Col, Row, Statistic, Popover, Alert} from 'antd';
import {timestampToDateTime} from '../../interfaces/Functions'
import './css/ExecutionHistoryOverview.css'

export class SelectedExecutionOverview extends Component {
    constructor(props, context) {
        super(props, context);
    }

    render() {
        let time = 0
        let completionProgress = 0;
        let nodeIds = Object.keys(this.props.selectedHistory.nodes)
        for (let i = 0; i < nodeIds.length; i++) {
            if (this.props.selectedHistory.nodes[nodeIds[i]].usedTime) {
                time = time + this.props.selectedHistory.nodes[nodeIds[i]].usedTime
                
            }
            if (this.props.selectedHistory.nodes[nodeIds[i]].status && this.props.selectedHistory.nodes[nodeIds[i]].status == 'FINISHED') {
                completionProgress = completionProgress + 1
            }
        }
        if (nodeIds.length > 0) {
            completionProgress = completionProgress / (nodeIds.length*1.00) * 100
        }

        return (
            <div className="execution-overview">
                <Card
                    style={{height: '100%'}}
                    title={<span className="execution-overview-card-title">Execution Overview</span>}>
                    
                    <div>
                        <Row gutter={16}>
                            <Col span={8}>
                                <Statistic title="Status" value={this.props.selectedHistory.status === 'CREATED' || this.props.selectedHistory.status === 'WAITING_TO_BE_SCHEDULED' ?
                                          'Waiting to be scheduled...':
                                                this.props.selectedHistory.status === 'RUNNING' ?
                                                'Running...':
                                                this.props.selectedHistory.status === 'FINISHED' ?
                                                'Finished':
                                                this.props.selectedHistory.status === 'FAILED' ?
                                                'Failed' : ''}></Statistic>
                            </Col>
                            <Col span={8}>
                                <Statistic title="Start Time" value={timestampToDateTime(new Date(this.props.selectedHistory.createdTime))}/>
                            </Col>
                        </Row>
                        <Row gutter={16} style={{marginTop: 18}}>
                            <Col span={8}>
                                <Statistic title="Already Used Time" value={time}/>
                            </Col>
                            <Col span={8}>
                                <Statistic title="Completed" suffix="%" precision={2} value={completionProgress}/>
                            </Col>
                        </Row>
                        <Row style={{marginTop: 66}}>
                            <Alert style={{textAlign: 'center', margin: 'auto'}} message="Click the operator in the diagram to display its information" type="success" />
                        </Row>
                    </div>
                    
                </Card>
            </div>
        )
    }
}