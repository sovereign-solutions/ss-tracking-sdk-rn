module.exports = function (api) {
  api.cache(true);

  return {
    presets: ['module:metro-react-native-babel-preset'],
    plugins: [
      [
        'module-resolver',
        // {
        //   root: ['./src'],
        //   alias: {
        //     components: './src/components',
        //     assets: './src/assets',
        //     screens: './src/screens',
        //     services: './src/services',
        //     stores: './src/stores',
        //     utils: './src/utils',
        //     model: './src/model',
        //   },
        //   extensions: ['.js', '.jsx', '.tsx', '.ios.js', '.android.js'],
        // },
      ],
      // 'react-native-reanimated/plugin',
    ],
  };
};
