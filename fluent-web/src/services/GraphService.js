export function drawExecutionGraph(data) {
    let res = []
    let nodesHaveDrawn = {}
    let branchesNum = {}  // record nodes number in each level to split them in the graph
    if (data.sources.length > 0) {
        for (let i = 0; i < data.sources.length; i++) {
            drawNextNodes(data, null, data.sources[i], 1, branchesNum, res, nodesHaveDrawn);
            branchesNum['num'] = branchesNum['num'] + 1
        }
    }
    return res;
}

function drawNextNodes(data, parentId, id, pathLength, branchesNum, res, nodesHaveDrawn) {
    const node = data.nodes[id]
    if (!branchesNum['num']) {
        branchesNum['num'] = 1
    }

    if (!nodesHaveDrawn[id]) {
        let obj = {}
        Object.assign(obj, {
            id: id,
            data: {
                label: id + '-' + node.operator.name,
                name: node.operator.name,
                usedTime: node.usedTime
            },
            position: {x: 250 * pathLength, y: 100 * branchesNum['num']}
        })
        if (node.status === 'RUNNING') {
            obj['type'] = 'runningNode'
        } else if (node.status === 'FAILED') {
            obj['type'] = 'errorNode'
        } else if (node.status === 'FINISHED') {
            obj['type'] = 'finishedNode'
        } else {
            obj['type'] = 'todoNode'
        }
        res.push(obj)
        nodesHaveDrawn[id] = 'true'

        // draw the connections
        if (parentId) {
            res.push(
                {
                    id: parentId + '->' + id,
                    source: parentId,
                    target: id,
                    sourceHandler: null,
                    targetHandler: null,
                    arrowHeadType: 'arrowclosed',
                    type: 'custom',
                    animated: true
                }
            )
        }
    } else {
        let connectionExisted = false
        for (let i = 0; i < res.length; i++) {
            if (res[i].id === id) {
                res[i].position = {x: 250 * pathLength, y: 100 * branchesNum['num']}
            }
            if (res[i].id === parentId + '->' + id) {
                connectionExisted = true
            }
        }
        if (!connectionExisted && parentId) {
            res.push(
                {
                    id: parentId + '->' + id,
                    source: parentId,
                    target: id,
                    sourceHandler: null,
                    targetHandler: null,
                    arrowHeadType: 'arrowclosed',
                    type: 'custom',
                    animated: true
                }
            )
        }

    }


    if (node.next.length > 0) {
        for (let i = 0; i < node.next.length; i++) {
            branchesNum['num'] = branchesNum['num'] + i
            drawNextNodes(data, id, node.next[i], pathLength + 1, branchesNum, res, nodesHaveDrawn);
        }
    }
}
