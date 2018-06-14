import React, { Component } from 'react';
import { Modal, View, Text, ActivityIndicator, Dimensions } from 'react-native';
import PropTypes from 'prop-types';
export default class Loading extends Component {
  constructor(props) {
    super(props);
    this.state = {
      modalVisible: false,
    };
  }

  static defaultProps = {
    message: '正在执行...',
  };

  static propTypes = {
    message: PropTypes.string,
  };

  show() {
    this.setState({ modalVisible: true });
  }

  dismiss() {
    this.setState({ modalVisible: false });
  }

  render() {
    return (
      <Modal
        style={{
          zIndex: 1000,
        }}
        animationType="none"
        transparent
        visible={this.state.modalVisible}
        onRequestClose={() => {}}
        ref={(ref) => {
          this.modal = ref;
        }}
      >
        <View
          style={{
            backgroundColor: '#00000080',
            justifyContent: 'center',
            alignItems: 'center',
            flex: 1,
          }}
        >
          <View
            style={{
              width: Dimensions.get('window').width - 100,
              paddingVertical: 30,
              backgroundColor: '#ffffff',
              borderRadius: 5,
            }}
          >
            <ActivityIndicator
              color="#3DA7F4"
              size="small"
            />
            <Text
              style={{
                marginTop: 20,
                textAlign: 'center',
                color: '#333333',
              }}
            >
              {this.props.message}
            </Text>
          </View>
        </View>
      </Modal>
    );
  }
}
