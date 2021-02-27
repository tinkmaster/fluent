import React, {Component} from "react";
import {Col, List, Popover, Row} from "antd";
import {timestampToDateTime} from '../../interfaces/Functions'
import HourglassOutlined from "@ant-design/icons/lib/icons/HourglassOutlined";
import LoadingOutlined from "@ant-design/icons/lib/icons/LoadingOutlined";
import CheckOutlined from "@ant-design/icons/lib/icons/CheckOutlined";
import CloseOutlined from "@ant-design/icons/lib/icons/CloseOutlined";
import "./css/ExecutionHistoryList.css"
import { StopOutlined } from "@ant-design/icons";

export class ExecutionHistoryList extends Component {

    constructor(props, context) {
        super(props, context);
    }

    render() {
        return (
            <div className="execution-history-list">
                <List style={{backgroundColor: 'white', height: '100%'}}
                      header={<div className="execution-history-list-title">
                          <Row style={{'width': '100%', textAlign: 'center'}}>
                              <Col span={16}>
                                  <span>Execution History</span>
                              </Col>
                              <Col span={8}>
                                  <span>Status</span>
                              </Col>
                          </Row>
                      </div>}
                      dataSource={this.props.executionGraphList}
                      renderItem={item => (
                          <List.Item
                              onClick={() => this.props.selectExecution(item.name)}
                              style={{
                                  backgroundColor: this.props.selectedExecution ?
                                      this.props.selectedExecution.name === item.name ?
                                          "#e1e1e1" : null
                                      : null,
                                  paddingLeft: 24,
                                  paddingRight: 24
                              }}
                              className='execution-history-list-item'
                          >
                              <Row style={{'width': '100%', textAlign: 'center'}}>
                                  <Col span={16}>
                                      {timestampToDateTime(new Date(item.createdTime))}
                                  </Col>
                                  <Col span={8}>
                                        {item.status === 'CREATED' ?
                                            <Popover content={'Waiting to be scheduled...'}
                                                    trigger="hover">
                                                <HourglassOutlined/>
                                            </Popover>
                                            : ''}
                                        {item.status === 'RUNNING' ?
                                            <Popover content={'Running...'} trigger="hover">
                                                <LoadingOutlined/>
                                            </Popover>
                                            : ''}
                                        {item.status === 'FINISHED' ?
                                            <Popover content={'Finished'} trigger="hover">
                                                <CheckOutlined style={{color: 'green'}}/>
                                            </Popover>
                                            : ''}
                                        {item.status === 'SKIPPED' ?
                                          <Popover content={'Skipped'} trigger="hover">
                                              <StopOutlined style={{color: 'blue'}}/>
                                          </Popover>
                                          : ''}  
                                        {item.status === 'FAILED' ?
                                            <Popover content={'Failed'} trigger="hover">
                                                <CloseOutlined style={{color: 'red'}}/>
                                            </Popover>
                                            : ''}
                                  </Col>
                              </Row>
                          </List.Item>
                      )}/>
            </div>
        )
    }

}
