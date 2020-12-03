import React, {Component} from "react";
import {Card, Col, Row, Statistic} from 'antd';
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
                    <Row gutter={16}>
                        <Col span={8}>
                            <Statistic title="Build Times" value={112893}/>
                        </Col>
                        <Col span={8}>
                            <Statistic title="Latest Build" value={112893}/>
                        </Col>
                        <Col>
                            <Statistic title="Last Failed" value={112893} precision={2}/>
                        </Col>
                    </Row>
                </Card>
            </div>
        )
    }

}
